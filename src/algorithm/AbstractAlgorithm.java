/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import gui_timetabling.Line;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author quancq
 */
public abstract class AbstractAlgorithm {

    private String algoName;
    private HashMap<String, ArrayList<Double>> parameters;

    public AbstractAlgorithm(String algoName, HashMap<String, ArrayList<Double>> parameters) {
        this.algoName = algoName;
        this.parameters = parameters;
    }

    public HashMap<String, ArrayList<Double>> getParameters() {
        return parameters;
    }

    public String getAlgoName() {
        return algoName;
    }

    public void setParameters(HashMap<String, ArrayList<Double>> parameters) {
        this.parameters = parameters;
    }

    public void setAlgoName(String algoName) {
        this.algoName = algoName;
    }

    @Override
    public String toString() {
        return "AbstractAlgorithm{" + "algoName=" + algoName + ", parameters=" + parameters + '}';
    }

    public abstract HashMap<String, Line> runAlgo(HashMap<String, Double> parameter);
}
