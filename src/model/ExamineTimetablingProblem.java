/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

/**
 *
 * @author DungBachViet
 */
public class ExamineTimetablingProblem {
    
    public HashMap<String, Area> hmIDToArea;
    public HashMap<String, Course> hmIDToCourse;
    public HashMap<String, ExamClass> hmIDToExamClass;
    public HashMap<String, Room> hmIDToRoom;
    public HashMap<String, Teacher> hmIDToTeacher;
   
    public HashMap<Integer, ExamClass> hmIntegerToExamClass;
    
    public ArrayList<Integer> availableDayList;            // ds các ngày có thể tổ chức thi
    public ArrayList<Integer> jamLevelList;                // ds mức độ tắc nghẽn của các kíp thi
    
    
    public int[][] commonStudents;
    public int numExamClass;
    public int numTeacher;
    public int numCourse;
    public int numRoom;
    public int numArea;
    
    Area[] areas;
    Course[] courses;
    ExamClass[] examClasses;
    Room[] rooms;
    Teacher[] teachers;
    
    
    LocalSearchManager ls;
    ConstraintSystem S;
    
    VarIntLS[] examDays;
    VarIntLS[] examTimeSlots;
    VarIntLS[] examRooms;
    VarIntLS[][] examClassToTeacher;
    
    public void stateModel() {
        
        for (int index = 0; index < numExamClass; index++) {
            examDays[index] = new VarIntLS(ls, )
        }
    }
    
    
    
    
    
    public static void main(String[] args) {
        System.out.println("");
    }
      
}
