/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author quancq
 */
public class ExamineTimetablingManager implements Serializable {

    private static final long serialVersionUID = 1L;
    private HashMap<String, Area> hmIDToArea;
    private HashMap<String, Course> hmIDToCourse;
    private HashMap<String, ExamClass> hmIDToExamClass;
    private HashMap<String, Room> hmIDToRoom;
    private HashMap<String, Teacher> hmIDToTeacher;

    private ArrayList<Integer> availableDayList;            // ds các ngày có thể tổ chức thi
    private ArrayList<Integer> jamLevelList;                // ds mức độ tắc nghẽn của các kíp thi

    public ExamineTimetablingManager() {
    }

    public HashMap<String, Area> getHmIDToArea() {
        return hmIDToArea;
    }

    public HashMap<String, Course> getHmIDToCourse() {
        return hmIDToCourse;
    }

    public HashMap<String, ExamClass> getHmIDToExamClass() {
        return hmIDToExamClass;
    }

    public HashMap<String, Room> getHmIDToRoom() {
        return hmIDToRoom;
    }

    public HashMap<String, Teacher> getHmIDToTeacher() {
        return hmIDToTeacher;
    }

    public ArrayList<Integer> getAvailableDayList() {
        return availableDayList;
    }

    public ArrayList<Integer> getJamLevelList() {
        return jamLevelList;
    }

    public void setHmIDToArea(HashMap<String, Area> hmIDToArea) {
        this.hmIDToArea = hmIDToArea;
    }

    public void setHmIDToCourse(HashMap<String, Course> hmIDToCourse) {
        this.hmIDToCourse = hmIDToCourse;
    }

    public void setHmIDToExamClass(HashMap<String, ExamClass> hmIDToExamClass) {
        this.hmIDToExamClass = hmIDToExamClass;
    }

    public void setHmIDToRoom(HashMap<String, Room> hmIDToRoom) {
        this.hmIDToRoom = hmIDToRoom;
    }

    public void setHmIDToTeacher(HashMap<String, Teacher> hmIDToTeacher) {
        this.hmIDToTeacher = hmIDToTeacher;
    }

    public void setAvailableDayList(ArrayList<Integer> availableDayList) {
        this.availableDayList = availableDayList;
    }

    public void setJamLevelList(ArrayList<Integer> jamLevelList) {
        this.jamLevelList = jamLevelList;
    }

}
