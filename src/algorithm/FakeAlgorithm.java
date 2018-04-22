/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import gui_timetabling.Line;
import java.util.ArrayList;
import java.util.HashMap;
import static util.UtilManager.randomDoubleList;

/**
 *
 * @author quancq
 */
public class FakeAlgorithm extends AbstractAlgorithm {

    public FakeAlgorithm(String algoName, HashMap<String, ArrayList<Double>> parameters) {
        super(algoName, parameters);
    }

    @Override
    public HashMap<String, Line> runAlgo(HashMap<String, Double> parameter) {
        HashMap<String, Line> hm = new HashMap<>();

        double tabuLen = parameter.get("TabuLength");
        double maxIter = parameter.get("maxIter");

        ArrayList<Double> xList = randomDoubleList(25, 0, 10);
        ArrayList<Double> yList = randomDoubleList(25, 0, 100);

        Line lineViolation = new Line("Violation", xList, yList);
        hm.put(lineViolation.getLineName(), lineViolation);
        
        xList = randomDoubleList(25, 0, 10);
        yList = randomDoubleList(25, 0, 100);

        Line lineFitness = new Line("Fitness", xList, yList);
        hm.put(lineFitness.getLineName(), lineFitness);

        return hm;
    }

}
