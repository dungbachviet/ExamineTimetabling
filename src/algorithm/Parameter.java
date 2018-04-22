/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author quancq
 */
public class Parameter {

    private HashMap<String, ArrayList<Double>> parameters;

    public Parameter(HashMap<String, ArrayList<Double>> parameters) {
        this.parameters = parameters;
    }

    public Parameter() {
        parameters = new HashMap<>();
    }

    public HashMap<String, ArrayList<Double>> getParameters() {
        return parameters;
    }

    public ArrayList<Double> getParameter(String paraName) {
        return parameters.get(paraName);
    }

    public void setParameters(HashMap<String, ArrayList<Double>> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String paraName, ArrayList<Double> listValue) {
        parameters.put(paraName, listValue);
    }
}
