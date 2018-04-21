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

    public ArrayList<Area> getAreaList() {
        ArrayList<Area> list = new ArrayList<>();
        for (String id : hmIDToArea.keySet()) {
            list.add(hmIDToArea.get(id));
        }
        return list;
    }

    public ArrayList<Course> getCourseList() {
        ArrayList<Course> list = new ArrayList<>();
        for (String id : hmIDToCourse.keySet()) {
            list.add(hmIDToCourse.get(id));
        }
        return list;
    }

    public ArrayList<ExamClass> getExamClassList() {
        ArrayList<ExamClass> list = new ArrayList<>();
        for (String id : hmIDToExamClass.keySet()) {
            list.add(hmIDToExamClass.get(id));
        }
        return list;
    }

    public ArrayList<Room> getRoomList() {
        ArrayList<Room> list = new ArrayList<>();
        for (String id : hmIDToRoom.keySet()) {
            list.add(hmIDToRoom.get(id));
        }
        return list;
    }

    public ArrayList<Teacher> getTeacherList() {
        ArrayList<Teacher> list = new ArrayList<>();
        for (String id : hmIDToTeacher.keySet()) {
            list.add(hmIDToTeacher.get(id));
        }
        return list;
    }

    public ArrayList<Integer> getAvailableDayList() {
        return availableDayList;
    }

    public ArrayList<Integer> getJamLevelList() {
        return jamLevelList;
    }

    public int getNumAreas(){
        return hmIDToArea.size();
    }
    
    public int getNumCourses(){
        return hmIDToCourse.size();
    }
    
    public int getNumExamClasses(){
        return hmIDToExamClass.size();
    }
    
    public int getNumRooms(){
        return hmIDToRoom.size();
    }
    
    public int getNumTeachers(){
        return hmIDToTeacher.size();
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

    public int[][] calcNumberCommonStudentOfClasses() {
        ArrayList<ExamClass> examClassList = getExamClassList();
        int numExamClasses = examClassList.size();
        int[][] result = new int[numExamClasses][numExamClasses];

        for (int i = 0; i < numExamClasses - 1; ++i) {
            for (int j = i + 1; j < numExamClasses; ++j) {
                // count common students
                ExamClass class1 = examClassList.get(i);
                ExamClass class2 = examClassList.get(j);
                
                ArrayList<String> enrollmentList1 = class1.getEnrollmentList();
                ArrayList<String> enrollmentList2 = class2.getEnrollmentList();
                int numCommonStudents = 0;
                for(String studentID : enrollmentList1){
                    if(enrollmentList2.contains(studentID)){
                        numCommonStudents++;
                    }
                }
                
                result[i][j] = numCommonStudents;
                result[j][i] = result[i][j];
            }
        }

        return result;
    }

}
