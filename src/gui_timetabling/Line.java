/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_timetabling;

import java.util.ArrayList;

/**
 *
 * @author quancq
 */
public class Line {

    private String lineName;
    private ArrayList<Double> xList;
//    private String xLabel;
    private ArrayList<Double> yList;
//    private String yLabel;

    public Line(String lineName, ArrayList<Double> xList, ArrayList<Double> yList) {
        this.lineName = lineName;
        this.xList = xList;
        this.yList = yList;
    }

    public String getLineName() {
        return lineName;
    }

    public ArrayList<Double> getxList() {
        return xList;
    }

    public ArrayList<Double> getyList() {
        return yList;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public void setxList(ArrayList<Double> xList) {
        this.xList = xList;
    }

    public void setyList(ArrayList<Double> yList) {
        this.yList = yList;
    }

    public void addPoint(double x, double y){
        xList.add(x);
        yList.add(y);
    }
}
