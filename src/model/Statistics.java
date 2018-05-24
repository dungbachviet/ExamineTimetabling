/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gui_timetabling.Line;
import gui_timetabling.MultipleLinesChart;
import java.util.ArrayList;
import util.DataIO;
import static model.ExamineTimetablingProblem.*;
import util.UtilManager;

/**
 *
 * @author quancq
 */
public class Statistics {

    public static ExamineTimetablingProblem Timetabling;

    public static void main(String[] args) {
        Timetabling = new ExamineTimetablingProblem();
        System.out.println(Timetabling.manager.toString());

        // Set up problem's model
//        Timetabling.stateModel();
//        
//        Timetabling.testBatchAnnealing(1);
        separateAlgorithmStatistic();

    }

    public static void separateAlgorithmStatistic() {
        String dirSaveFile = "src/statistics/" + System.currentTimeMillis();

        // choose algorithm
//        tabuStatistic(dirSaveFile);
//        annealingStatistic(dirSaveFile);
        degratedCeilingStatistic(dirSaveFile);
        
        //show solution
        Timetabling.showSolution();

        // write info data
        String infoDataPath = dirSaveFile + "/info_data.txt";
        DataIO.writeStringToFile(infoDataPath, Timetabling.manager.toString());
        DataIO.writeObject(dirSaveFile + "/data", Timetabling.manager);

    }

    public static void tabuStatistic(String dirSaveFile) {

        int[] tabuLenArr = new int[]{30};
        int[] maxTimeArr = new int[]{30};
        int[] maxIterArr = new int[]{500};
        int[] maxStableArr = new int[]{200};
        int numParameters = tabuLenArr.length;
        int numTest = 1;
        
        
//        int[] tabuLenArr = new int[]{40};
//        int[] maxTimeArr = new int[]{70};
//        int[] maxIterArr = new int[]{20};
//        int[] maxStableArr = new int[]{300};
//        int numParameters = tabuLenArr.length;
//        int numTest = 1;

        ArrayList<Line> lineViolationList = new ArrayList<>();
        ArrayList<Line> lineFitnessList = new ArrayList<>();

        if (dirSaveFile == null || dirSaveFile.length() == 0) {
            dirSaveFile = "src/statistics/" + System.currentTimeMillis();
        }
        DataIO.makeDir(dirSaveFile);

        ArrayList<ArrayList<String>> outputList = new ArrayList<>();
        ArrayList<String> colName = new ArrayList<>();
        colName.add("ParameterID");
        colName.add("TabuLen");
        colName.add("maxTime");
        colName.add("maxIter");
        colName.add("maxStable");
        colName.add("MinFitness");
        colName.add("AverageFitness");
        colName.add("MaxFitness");
        colName.add("VarianceFitness");
        colName.add("MinTime");
        colName.add("AverageTime");
        colName.add("MaxTime");
        colName.add("VarianceTime");

        outputList.add(colName);

        for (int indexPara = 0; indexPara < numParameters; ++indexPara) {
            String paraID = "Para" + (indexPara + 1);

            // set parameter
            tabulen = tabuLenArr[indexPara];
            maxTime = maxTimeArr[indexPara];
            maxIter = maxIterArr[indexPara];
            maxStable = maxStableArr[indexPara];

            ExamineTimetablingProblem.testBatchTabu(numTest, Timetabling);

            ArrayList<String> row1 = new ArrayList<>();

//                row1.add("Parameter1");
            row1.add(paraID);
            row1.add(String.valueOf(tabulen));
            row1.add(String.valueOf(maxTime));
            row1.add(String.valueOf(maxIter));
            row1.add(String.valueOf(maxStable));

            double minFitness = UtilManager.getMin(bestFitness);
            double avgFitness = UtilManager.getAverage(bestFitness);
            double maxFitness = UtilManager.getMax(bestFitness);
            double varFitness = UtilManager.getVariance(bestFitness);

            double minTime = UtilManager.getMin(timeRun);
            double avgTime = UtilManager.getAverage(timeRun);
            double maxTime = UtilManager.getMax(timeRun);
            double varTime = UtilManager.getVariance(timeRun);

            row1.add(String.valueOf(minFitness));
            row1.add(String.valueOf(avgFitness));
            row1.add(String.valueOf(maxFitness));
            row1.add(String.valueOf(varFitness));

            System.out.println("\n==>" + timeRun);
            row1.add(String.valueOf(minTime));
            row1.add(String.valueOf(avgTime));
            row1.add(String.valueOf(maxTime));
            row1.add(String.valueOf(varTime));
            System.out.println("\nRow1 = " + row1);

            outputList.add(row1);

            // Violation
            ArrayList<Double> xList = new ArrayList<>();
            ArrayList<Double> yList = new ArrayList<>();

            int index = 0;
            yList = violationList.get(index);

            for (int k = 0; k < yList.size(); ++k) {
                xList.add(1.0 * (k + 1));
            }

            Line lineViolation = new Line(paraID, xList, yList);
            lineViolationList.add(lineViolation);

            // Fitness phase 2
            ArrayList<Double> xList2 = new ArrayList<>();
            ArrayList<Double> yList2 = new ArrayList<>();

            int index2 = 0;
            yList2 = fitnessList2.get(index2);

            for (int k = 0; k < yList2.size(); ++k) {
                xList2.add(1.0 * (k + 1));
            }

            Line lineFitness2 = new Line(paraID + "_Phase2", xList2, yList2);
            lineFitnessList.add(lineFitness2);
            
            // Fitness phase 3
            ArrayList<Double> xList3 = new ArrayList<>();
            ArrayList<Double> yList3 = new ArrayList<>();

            int index3 = 0;
            yList3 = fitnessList3.get(index3);

            for (int k = 0; k < yList3.size(); ++k) {
                xList3.add(1.0 * (k + 1 + yList2.size()));
            }

            Line lineFitness3 = new Line(paraID + "_Phase3", xList3, yList3);
            lineFitnessList.add(lineFitness3);

        }
        boolean isShowGui = true;
        if (isShowGui) {
            // show violation_loop
            MultipleLinesChart mlc = new MultipleLinesChart("Demo");
            mlc.setxAxisLabel("Loop");
            mlc.setyAxisLabel("Violation");
            mlc.setChartTitle("Tabu algorithm");

            mlc.setLineList(lineViolationList);

            String pathSaveImage = dirSaveFile + "/violation-loop_Tabu.png";
            mlc.renderGraph(false, pathSaveImage);

            // show violation_loop
            MultipleLinesChart mlc2 = new MultipleLinesChart("Demo");
            mlc2.setxAxisLabel("Loop");
            mlc2.setyAxisLabel("Violation");
            mlc2.setChartTitle("Tabu algorithm");

            mlc2.setLineList(lineFitnessList);

            String pathSaveImage2 = dirSaveFile + "/fitness-loop_Tabu.png";
            mlc2.renderGraph(false, pathSaveImage2);
        }

        // write excel file
        String csvPath = dirSaveFile + "/Tabu_NumTest-" + numTest + ".csv";
        DataIO.writeFileExcel(csvPath, outputList);

        // write infomation data file
//        String infoDataPath = dirSaveFile + "/info_data.txt";
//        DataIO.writeStringToFile(infoDataPath, Timetabling.manager.toString());
    }

    public static void annealingStatistic(String dirSaveFile) {

//        int[] maxTimeArr = new int[]{200, 200};
//        int[] maxIterArr = new int[]{300000, 500000};
//        double[] initialTempArr = new double[]{5000, 7000};
//        double[] endingTempArr = new double[]{0.05, 0.01};
        
        int[] maxTimeArr = new int[]{200};
        int[] maxIterArr = new int[]{10000};
        double[] initialTempArr = new double[]{0.05};
        double[] endingTempArr = new double[]{0.0005};
        int numParameters = maxTimeArr.length;
        int numTest = 1;
        
        
//        int[] maxTimeArr = new int[]{800};
//        int[] maxIterArr = new int[]{400};
//        double[] initialTempArr = new double[]{7000};
//        double[] endingTempArr = new double[]{0.05};
//        int numParameters = maxTimeArr.length;
//        int numTest = 2;
        

        ArrayList<Line> lineViolationList = new ArrayList<>();
        ArrayList<Line> lineFitnessList = new ArrayList<>();

        if (dirSaveFile == null || dirSaveFile.length() == 0) {
            dirSaveFile = "src/statistics/" + System.currentTimeMillis();
        }
        DataIO.makeDir(dirSaveFile);

        ArrayList<ArrayList<String>> outputList = new ArrayList<>();
        ArrayList<String> colName = new ArrayList<>();
        colName.add("ParameterID");
        colName.add("maxTime");
        colName.add("maxIter");
        colName.add("initialTemp");
        colName.add("endingTemp");
        colName.add("MinFitness");
        colName.add("AverageFitness");
        colName.add("MaxFitness");
        colName.add("VarianceFitness");
        colName.add("MinTime");
        colName.add("AverageTime");
        colName.add("MaxTime");
        colName.add("VarianceTime");

        outputList.add(colName);

        for (int indexPara = 0; indexPara < numParameters; ++indexPara) {
            String paraID = "Para" + (indexPara + 1);

            // set parameter
            maxTime = maxTimeArr[indexPara];
            maxIter = maxIterArr[indexPara];
            initialTemp = initialTempArr[indexPara];
            endingTemp = endingTempArr[indexPara];

            ExamineTimetablingProblem.testBatchAnnealing(numTest, Timetabling);

            ArrayList<String> row1 = new ArrayList<>();

//                row1.add("Parameter1");
            row1.add(paraID);
            row1.add(String.valueOf(maxTime));
            row1.add(String.valueOf(maxIter));
            row1.add(String.valueOf(initialTemp));
            row1.add(String.valueOf(endingTemp));

            double minFitness = UtilManager.getMin(bestFitness);
            double avgFitness = UtilManager.getAverage(bestFitness);
            double maxFitness = UtilManager.getMax(bestFitness);
            double varFitness = UtilManager.getVariance(bestFitness);

            double minTime = UtilManager.getMin(timeRun);
            double avgTime = UtilManager.getAverage(timeRun);
            double maxTime = UtilManager.getMax(timeRun);
            double varTime = UtilManager.getVariance(timeRun);

            row1.add(String.valueOf(minFitness));
            row1.add(String.valueOf(avgFitness));
            row1.add(String.valueOf(maxFitness));
            row1.add(String.valueOf(varFitness));

            System.out.println("\n==>" + timeRun);
            row1.add(String.valueOf(minTime));
            row1.add(String.valueOf(avgTime));
            row1.add(String.valueOf(maxTime));
            row1.add(String.valueOf(varTime));
            System.out.println("\nRow1 = " + row1);

            outputList.add(row1);

            // Violation
            ArrayList<Double> xList = new ArrayList<>();
            ArrayList<Double> yList = new ArrayList<>();

            int index = 0;
            yList = violationList.get(index);

            for (int k = 0; k < yList.size(); ++k) {
                xList.add(1.0 * (k + 1));
            }

            Line lineViolation = new Line(paraID, xList, yList);
            lineViolationList.add(lineViolation);

            // Fitness phase 2
            ArrayList<Double> xList2 = new ArrayList<>();
            ArrayList<Double> yList2 = new ArrayList<>();

            int index2 = 0;
            yList2 = fitnessList2.get(index2);

            for (int k = 0; k < yList2.size(); ++k) {
                xList2.add(1.0 * (k + 1));
            }

            Line lineFitness2 = new Line(paraID + "_Phase2", xList2, yList2);
            lineFitnessList.add(lineFitness2);
            
            // Fitness phase 3
            ArrayList<Double> xList3 = new ArrayList<>();
            ArrayList<Double> yList3 = new ArrayList<>();

            int index3 = 0;
            yList3 = fitnessList3.get(index3);

            for (int k = 0; k < yList3.size(); ++k) {
                xList3.add(1.0 * (k + 1 + yList2.size()));
            }

            Line lineFitness3 = new Line(paraID + "_Phase3", xList3, yList3);
            lineFitnessList.add(lineFitness3);
        }
        boolean isShowGui = true;
        if (isShowGui) {
            // show violation_loop
            MultipleLinesChart mlc = new MultipleLinesChart("Demo");
            mlc.setxAxisLabel("Loop");
            mlc.setyAxisLabel("Violation");
            mlc.setChartTitle("Annealing algorithm");

            mlc.setLineList(lineViolationList);

            String pathSaveImage = dirSaveFile + "/violation-loop_Annealing.png";
            mlc.renderGraph(false, pathSaveImage);

            // show violation_loop
            MultipleLinesChart mlc2 = new MultipleLinesChart("Demo");
            mlc2.setxAxisLabel("Loop");
            mlc2.setyAxisLabel("Violation");
            mlc2.setChartTitle("Annealing algorithm");

            mlc2.setLineList(lineFitnessList);

            String pathSaveImage2 = dirSaveFile + "/fitness-loop_Annealing.png";
            mlc2.renderGraph(false, pathSaveImage2);
        }

        // write excel file
        String csvPath = dirSaveFile + "/Annealing_NumTest-" + numTest + ".csv";
        DataIO.writeFileExcel(csvPath, outputList);

        // write infomation data file
//        String infoDataPath = dirSaveFile + "/info_data.txt";
//        DataIO.writeStringToFile(infoDataPath, Timetabling.manager.toString());
    }

    public static void degratedCeilingStatistic(String dirSaveFile) {

//        int[] maxTimeArr = new int[]{200, 200};
////        int[] maxIterArr = new int[]{300000, 500000};
//        double[] desiredFitnessArr = new double[]{0.45, 0.55};
//        int numParameters = maxTimeArr.length;
//        int numTest = 2;

        int[] maxTimeArr = new int[]{60};
        int[] maxIterArr = new int[]{20000};
        double[] desiredFitnessArr = new double[]{0.5};
        int numParameters = maxTimeArr.length;
        int numTest = 1;
        
        
        ArrayList<Line> lineViolationList = new ArrayList<>();
        ArrayList<Line> lineFitnessList = new ArrayList<>();

        if (dirSaveFile == null || dirSaveFile.length() == 0) {
            dirSaveFile = "src/statistics/" + System.currentTimeMillis();
        }
        DataIO.makeDir(dirSaveFile);

        ArrayList<ArrayList<String>> outputList = new ArrayList<>();
        ArrayList<String> colName = new ArrayList<>();
        colName.add("ParameterID");
        colName.add("maxTime");
        colName.add("maxIter");
        colName.add("desiredFitness");
        colName.add("MinFitness");
        colName.add("AverageFitness");
        colName.add("MaxFitness");
        colName.add("VarianceFitness");
        colName.add("MinTime");
        colName.add("AverageTime");
        colName.add("MaxTime");
        colName.add("VarianceTime");

        outputList.add(colName);

        for (int indexPara = 0; indexPara < numParameters; ++indexPara) {
            String paraID = "Para" + (indexPara + 1);

            // set parameter
            maxTime = maxTimeArr[indexPara];
            maxIter = maxIterArr[indexPara];
            desiredFitness = desiredFitnessArr[indexPara];

            ExamineTimetablingProblem.testBatchDegratedCeiling(numTest, Timetabling);

            ArrayList<String> row1 = new ArrayList<>();

//                row1.add("Parameter1");
            row1.add(paraID);
            row1.add(String.valueOf(maxTime));
            row1.add(String.valueOf(maxIter));
            row1.add(String.valueOf(desiredFitness));

            double minFitness = UtilManager.getMin(bestFitness);
            double avgFitness = UtilManager.getAverage(bestFitness);
            double maxFitness = UtilManager.getMax(bestFitness);
            double varFitness = UtilManager.getVariance(bestFitness);

            double minTime = UtilManager.getMin(timeRun);
            double avgTime = UtilManager.getAverage(timeRun);
            double maxTime = UtilManager.getMax(timeRun);
            double varTime = UtilManager.getVariance(timeRun);

            row1.add(String.valueOf(minFitness));
            row1.add(String.valueOf(avgFitness));
            row1.add(String.valueOf(maxFitness));
            row1.add(String.valueOf(varFitness));

            System.out.println("\n==>" + timeRun);
            row1.add(String.valueOf(minTime));
            row1.add(String.valueOf(avgTime));
            row1.add(String.valueOf(maxTime));
            row1.add(String.valueOf(varTime));
            System.out.println("\nRow1 = " + row1);

            outputList.add(row1);

            // Violation
            ArrayList<Double> xList = new ArrayList<>();
            ArrayList<Double> yList = new ArrayList<>();

            int index = 0;
            yList = violationList.get(index);

            for (int k = 0; k < yList.size(); ++k) {
                xList.add(1.0 * (k + 1));
            }

            Line lineViolation = new Line(paraID, xList, yList);
            lineViolationList.add(lineViolation);

            // Fitness phase 2
            ArrayList<Double> xList2 = new ArrayList<>();
            ArrayList<Double> yList2 = new ArrayList<>();

            int index2 = 0;
            yList2 = fitnessList2.get(index2);

            for (int k = 0; k < yList2.size(); ++k) {
                xList2.add(1.0 * (k + 1));
            }

            Line lineFitness2 = new Line(paraID + "_Phase2", xList2, yList2);
            lineFitnessList.add(lineFitness2);
            
            // Fitness phase 3
            ArrayList<Double> xList3 = new ArrayList<>();
            ArrayList<Double> yList3 = new ArrayList<>();

            int index3 = 0;
            yList3 = fitnessList3.get(index3);

            for (int k = 0; k < yList3.size(); ++k) {
                xList3.add(1.0 * (k + 1 + yList2.size()));
            }

            Line lineFitness3 = new Line(paraID + "_Phase3", xList3, yList3);
            lineFitnessList.add(lineFitness3);

        }
        boolean isShowGui = true;
        if (isShowGui) {
            // show violation_loop
            MultipleLinesChart mlc = new MultipleLinesChart("Demo");
            mlc.setxAxisLabel("Loop");
            mlc.setyAxisLabel("Violation");
            mlc.setChartTitle("Degrated Ceiling algorithm");

            mlc.setLineList(lineViolationList);

            String pathSaveImage = dirSaveFile + "/violation-loop_DegratedCeiling.png";
            mlc.renderGraph(false, pathSaveImage);

            // show violation_loop
            MultipleLinesChart mlc2 = new MultipleLinesChart("Demo");
            mlc2.setxAxisLabel("Loop");
            mlc2.setyAxisLabel("Violation");
            mlc2.setChartTitle("Degrated Ceiling algorithm");

            mlc2.setLineList(lineFitnessList);

            String pathSaveImage2 = dirSaveFile + "/fitness-loop_DegratedCeiling.png";
            mlc2.renderGraph(false, pathSaveImage2);
        }

        // write excel file
        String csvPath = dirSaveFile + "/DegratedCeiling_NumTest-" + numTest + ".csv";
        DataIO.writeFileExcel(csvPath, outputList);

        // write infomation data file
//        String infoDataPath = dirSaveFile + "/info_data.txt";
//        DataIO.writeStringToFile(infoDataPath, Timetabling.manager.toString());
    }

}
