/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import gui_timetabling.Line;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import model.ExamineTimetablingManager;
import util.UtilManager;

/**
 *
 * @author quancq
 */
public abstract class AbstractAlgorithm {

    private String algoName;
    private Parameter parameter;
    private ArrayList<HashMap<String, Double>> applyParameter;
    private ExamineTimetablingManager etm;

    public AbstractAlgorithm(String algoName, Parameter parameter) {
        this.algoName = algoName;
        this.parameter = parameter;
    }

    public AbstractAlgorithm(String algoName) {
        this.algoName = algoName;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public String getAlgoName() {
        return algoName;
    }

    public ArrayList<HashMap<String, Double>> getApplyParameterList() {

        if (isGridSearch()) {
            System.out.println("\nGenerate grid search parameter\n");

            ArrayList<String> nameParameterList = new ArrayList<>();
            ArrayList<ArrayList<Double>> domain = new ArrayList<>();
            HashMap<String, ArrayList<Double>> hmNameToParameter = parameter.getParameters();

            for (String namePara : hmNameToParameter.keySet()) {
                nameParameterList.add(namePara);
                domain.add(hmNameToParameter.get(namePara));
            }

            applyParameter = new ArrayList<>();

            int domainSize = domain.size();
            int i;
            int j = domainSize - 1;
            double[] currSelectedValue = new double[domainSize];
            Iterator[] iters = new Iterator[domainSize];

            // init
            for (int k = 0; k < currSelectedValue.length; ++k) {
                currSelectedValue[k] = domain.get(k).get(0);
                iters[k] = domain.get(k).iterator();
            }
            
            HashSet<HashMap<String, Double>> hs = new HashSet<>();

            while (true) {

                while (iters[j].hasNext()) {
//                    String currParameterName = nameParameterList.get(i);
//                        Double currValue = (Double)(iters[j].next());
//                        currSelectedValue[j] = currValue;
                    currSelectedValue[j] = (Double) (iters[j].next());
                    HashMap<String, Double> hm = UtilManager.createHashMapFromList(
                            nameParameterList, currSelectedValue);
//                    applyParameter.add(hm);
                    hs.add(hm);

                }
                // check all iters k that i < k < j, find last k that iters[k] has next
                i = j - 1;
                while (i >= 0) {
                    if (iters[i].hasNext()) {
                        currSelectedValue[i] = (Double) (iters[i].next());
                        break;
                    }
                    --i;
                }
                if (i < 0) {
                    // not found 
                    break;
                } else {
                    // found
                    // reset all iter after i
                    for(int k = i+1; k < domainSize; ++k){
                        currSelectedValue[k] = domain.get(k).get(0);
                        iters[k] = domain.get(k).iterator();
                    }
                }

            }
            applyParameter = new ArrayList<>(hs);
        }
        System.out.println("\nNumber parameter: " + applyParameter.size());

        return applyParameter;
    }

    public void setApplyParameter(ArrayList<HashMap<String, Double>> applyParameter) {
        this.applyParameter = applyParameter;
    }

    public ExamineTimetablingManager getEtm() {
        return etm;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public void setAlgoName(String algoName) {
        this.algoName = algoName;
    }

    public void setEtm(ExamineTimetablingManager etm) {
        this.etm = etm;
    }

    public boolean isGridSearch() {
        return applyParameter == null || applyParameter.isEmpty();
    }

    @Override
    public String toString() {
        return "AbstractAlgorithm{" + "algoName=" + algoName + ", parameter=" + parameter + '}';
    }

    public abstract HashMap<String, Line> runAlgo(HashMap<String, Double> parameter);

    public abstract void stateModel();

}
