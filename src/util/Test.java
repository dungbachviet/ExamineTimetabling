/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import algorithm.AbstractAlgorithm;
import algorithm.FakeAlgorithm;
import algorithm.Parameter;
import gui_timetabling.Line;
import gui_timetabling.MultipleLinesChart;
import java.util.ArrayList;
import java.util.HashMap;
import model.ExamineTimetablingManager;
import static util.UtilManager.randomDouble;

/**
 *
 * @author quancq
 */
public class Test {

    public static void main(String[] args) {

        // define algo 1
        Parameter para1 = new Parameter();
        FakeAlgorithm fa1 = new FakeAlgorithm("FakeAlgo", para1);

        double[] list1 = {100.0, 400.0};
        ArrayList<Double> listValue1 = new ArrayList<>();
        for (double v : list1) {
            listValue1.add(v);
        }
        para1.addParameter("TabuLength", listValue1);

        double[] list2 = {40.0, 150.0};
        ArrayList<Double> listValue2 = new ArrayList<>();
        for (double v : list2) {
            listValue2.add(v);
        }
        para1.addParameter("maxIter", listValue2);

        // define algo 2
        Parameter para2 = new Parameter();
        FakeAlgorithm fa2 = new FakeAlgorithm("FakeAlgo", para2);

        double[] list3 = {50.0, 20.0};
        ArrayList<Double> listValue3 = new ArrayList<>();
        for (double v : list3) {
            listValue3.add(v);
        }
        para2.addParameter("TabuLength", listValue3);

        double[] list4 = {60.0, 45.0};
        ArrayList<Double> listValue4 = new ArrayList<>();
        for (double v : list4) {
            listValue4.add(v);
        }
        para2.addParameter("maxIter", listValue4);

        // create algo list
        ArrayList<AbstractAlgorithm> algoList = new ArrayList<>();
        algoList.add(fa1);
//        algoList.add(fa2);

        // Select dataset
        String path1 = "src/dataset_timetabling/test_data.txt";

        ExamineTimetablingManager etm1 = DataIO.readObject(path1);

        ArrayList<ExamineTimetablingManager> etmList = new ArrayList<>();
        etmList.add(etm1);

        int numTests = 1;
        runAlgoBatch(algoList, numTests, etmList, true, false);
    }

    public static void runAlgoBatch(
            ArrayList<AbstractAlgorithm> algoList,
            int numTests,
            ArrayList<ExamineTimetablingManager> etmList,
            boolean isSeparate,
            boolean isShowGui) {

        isShowGui = false;
        isSeparate = true;

        HashMap<String, HashMap<String, Double>> hmParameterIDToParameter = new HashMap<>();
        int id = 0;
        String tempTime = String.valueOf(System.currentTimeMillis());

        for (ExamineTimetablingManager etm : etmList) {
            // test each dataset
            String dataName = etm.getDatasetName();
            for (AbstractAlgorithm algo : algoList) {
                // test each algo
                String dirSaveStatistic = "src/statistics/"
                        + tempTime + "/" + dataName + "/Parameter_Selection/" + algo.getAlgoName();
                DataIO.makeDir(dirSaveStatistic);
                
                ArrayList<HashMap<String, Double>> parameterList = new ArrayList<>();
                parameterList = algo.getApplyParameterList();
                ArrayList<ArrayList<String>> statisticList = new ArrayList<>();
                ArrayList<String> listColName = new ArrayList<>();
                listColName.add("Parameter_ID");
                for (String key : parameterList.get(0).keySet()) {
                    listColName.add(key);
                }
                listColName.add("Min_Bestfiness");
                listColName.add("Avg_Bestfiness");
                listColName.add("Max_Bestfiness");
                listColName.add("Var_Bestfiness");
                listColName.add("Min_Time");
                listColName.add("Avg_Time");
                listColName.add("Max_Time");
                listColName.add("Var_Time");
                statisticList.add(listColName);

                for (HashMap<String, Double> singleParameter : parameterList) {
                    id++;
                    String parameterID = algo.getAlgoName() + "_" + id;
                    hmParameterIDToParameter.put(parameterID, singleParameter);

                    ArrayList<HashMap<String, Line>> outputList = new ArrayList<>();
                    for (int curTest = 1; curTest <= numTests; ++curTest) {
                        // run algo with specific parameter
                        HashMap<String, Line> output = algo.runAlgo(singleParameter);
                        outputList.add(output);

                        if (curTest <= 3) {
                            // save image fitness curve
                            ArrayList<Line> lineList = new ArrayList<>();
                            Line line = output.get("Fitness");
                            String nameLine = parameterID + "_NumTest" + curTest;
                            line.setLineName(nameLine);
                            String pathSaveImage = dirSaveStatistic + "/" + nameLine +".png";
                            
                            lineList.add(line);
                            saveImage(lineList, "Loop", "Fitness", parameterID,
                                    pathSaveImage, false);
                        }
                    }

                    // statistic of 1 parameter
                    ArrayList<String> statistic = handleOutputList(parameterID, singleParameter, outputList);

//                    System.out.println(statistic);
                    statisticList.add(statistic);

                }
                
                String pathSaveExcel = dirSaveStatistic + "/" + algo.getAlgoName() + ".csv";
                System.out.println("\nPath Excel = " + pathSaveExcel);
                DataIO.writeFileExcel(pathSaveExcel, statisticList);
            }
        }

    }

    /**
     * Hàm sẽ lưu ảnh đồ thị và return list gồm các string để ghi file excel
     *
     * @param parameterID
     * @param singleParameter
     * @param outputList là list output theo các lần test của 1 bộ tham số
     */
    private static ArrayList<String> handleOutputList(
            String parameterID,
            HashMap<String, Double> singleParameter,
            ArrayList<HashMap<String, Line>> outputList
    ) {

        // create images
//        MultipleLinesChart mlc = new MultipleLinesChart("Demo");
//        mlc.setxAxisLabel("Loop");
//        mlc.setyAxisLabel("Fitness");
//        mlc.setChartTitle("Tabu algorithm");
        ArrayList<Double> bestFitnessList = new ArrayList<>();
        ArrayList<Double> timeList = new ArrayList<>();

        for (HashMap<String, Line> output : outputList) {
            double bestFitness = output.get("BestFitness").getScalar();
            bestFitnessList.add(bestFitness);

            double time = output.get("Time").getScalar();
            timeList.add(time);
        }

        // create statistic info
        double minBestFitness = UtilManager.getMin(bestFitnessList);
        double maxBestFitness = UtilManager.getMax(bestFitnessList);
        double avgBestFitness = UtilManager.getAverage(bestFitnessList);
        double varBestFitness = UtilManager.getVariance(bestFitnessList);

        double minTime = UtilManager.getMin(timeList);
        double maxTime = UtilManager.getMax(timeList);
        double avgTime = UtilManager.getAverage(timeList);
        double varTime = UtilManager.getVariance(timeList);

        ArrayList<String> statisticList = new ArrayList<>();
        statisticList.add(parameterID);
        for (String namePara : singleParameter.keySet()) {

            statisticList.add(namePara + "=" + singleParameter.get(namePara));
        }

        statisticList.add(String.valueOf(minBestFitness));
        statisticList.add(String.valueOf(avgBestFitness));
        statisticList.add(String.valueOf(maxBestFitness));
        statisticList.add(String.valueOf(varBestFitness));

        statisticList.add(String.valueOf(minTime));
        statisticList.add(String.valueOf(avgTime));
        statisticList.add(String.valueOf(maxTime));
        statisticList.add(String.valueOf(varTime));

        return statisticList;

    }

    /**
     * Save one image
     *
     * @param xLabel
     * @param yLabel
     * @param chartTitle
     */
    private static void saveImage(
            ArrayList<Line> lineList, String xLabel, String yLabel,
            String chartTitle, String pathSaveImage, boolean isShowGui) {

        MultipleLinesChart mlc = new MultipleLinesChart("Demo");
        mlc.setxAxisLabel(xLabel);
        mlc.setyAxisLabel(yLabel);
        mlc.setChartTitle(chartTitle);

        mlc.setLineList(lineList);

//        pathSaveImage = "src/statistics/test_1.png";
        isShowGui = false;
//        DataIO.makeDir(pathSaveImage);
        mlc.renderGraph(isShowGui, pathSaveImage);
    }
}
