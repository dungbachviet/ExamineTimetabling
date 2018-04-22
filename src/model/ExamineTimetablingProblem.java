/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.element.Element;
import localsearch.functions.element.ElementTmp;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
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
    public int[] roomSlots;
    public int numExamClass;
    public int numTeacher;
    public int numCourse;
    public int numRoom;
    public int numArea;

    ArrayList<Area> areas;
    ArrayList<Course> courses;
    ArrayList<ExamClass> examClasses;
    ArrayList<Room> rooms;
    ArrayList<Teacher> teachers;
    Set<Integer> setAvailableDay;
    ArrayList<ArrayList<Integer>> sameCourseCodeClass;

    LocalSearchManager ls;
    ConstraintSystem S;

    VarIntLS[] examDays;
    VarIntLS[] examTimeSlots;
    VarIntLS[] examRooms;
    VarIntLS[][] examClassToTeacher;

    public void stateModel() {

        // Set up the value domains for variables 
        for (int index = 0; index < numExamClass; index++) {
            examDays[index] = new VarIntLS(ls, setAvailableDay);
            examTimeSlots[index] = new VarIntLS(ls, 0, 3);
            examRooms[index] = new VarIntLS(ls, 0, numExamClass - 1);
        }

        for (int examClassIndex = 0; examClassIndex < numExamClass; examClassIndex++) {
            for (int teacherIndex = 0; teacherIndex < 2; teacherIndex++) {
                examClassToTeacher[examClassIndex][teacherIndex] = new VarIntLS(ls, 0, numTeacher);
            }
        }

        // Room Capacity Constraint
        int numStudentPerClass;
        for (int examClassIndex = 0; examClassIndex < numExamClass; examClassIndex++) {
            numStudentPerClass = examClasses.get(examClassIndex).getEnrollmentList().size();
            S.post(new LessOrEqual(numStudentPerClass, new ElementTmp(roomSlots, examRooms[examClassIndex])));
        }

        // Constraint - same day and same time-slot if exam classes have the same course code
        int sameCourseCodeClasslength = sameCourseCodeClass.size();
        for (int courseIndex = 0; courseIndex < sameCourseCodeClasslength; courseIndex++) {
            ArrayList<Integer> listClasses = sameCourseCodeClass.get(courseIndex);
            int length = listClasses.size();
            VarIntLS[] listTimeSlots = new VarIntLS[length];
            VarIntLS[] listDays = new VarIntLS[length];
            for (int index = 0; index < length; index++) {
                listTimeSlots[index] = examTimeSlots[listClasses.get(index)];
                listDays[index] = examDays[listClasses.get(index)];
            }

            S.post(new AND(
                    new IsEqual(new Max(listTimeSlots), new Min(listTimeSlots)),
                    new IsEqual(new Max(listDays), new Min(listDays))));
        }
        
        
        // Constraint : Don't allow to violate the busy list of rooms
        for (int roomIndex = 0; roomIndex < numRoom; roomIndex++) {
            ArrayList<TimeUnit> busyList = rooms.get(roomIndex).getBusyTimeList();
            for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
                for (int busyIndex = 0; busyIndex < busyList.size(); busyIndex++) {
                    S.post(new Implicate(
                            new AND(
                                    new IsEqual(examRooms[classIndex], roomIndex),
                                    new IsEqual(examDays[classIndex], busyList.get(busyIndex).getDay())
                            ),
                            new NotEqual(examTimeSlots[classIndex], busyList.get(busyIndex).getTimeSlot())
                    ));
                }
            }
        }
        

        // Constraint : Don't allow to violate the busy list of teachers
        for (int teacherIndex = 0; teacherIndex < numTeacher; teacherIndex++) {
            ArrayList<TimeUnit> busyList = teachers.get(teacherIndex).getBusyTimeList();
            for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
                for (int busyIndex = 0; busyIndex < busyList.size(); busyIndex++) {
                    S.post(new Implicate(
                            new AND(
                                    new OR(new IsEqual(examClassToTeacher[classIndex][0], teacherIndex),
                                            new IsEqual(examClassToTeacher[classIndex][1], teacherIndex)),
                                    new IsEqual(examDays[classIndex], busyList.get(busyIndex).getDay())
                            ),
                            new NotEqual(examTimeSlots[classIndex], busyList.get(busyIndex).getTimeSlot())
                    ));
                }
            }
        }

        // Constraint : Don't allow any 2 exam classes to be placed at same room, same time
        // Constraint : Don't allow any 2 exam classes having any same teacher to be placed at same time
        // Constraint : Don't allow any 2 exam classes having any same student to be placed at same time
        VarIntLS[] convertIntToVarIntLS = new VarIntLS[2]; // convert integer to VarIntLS
        convertIntToVarIntLS[0] = new VarIntLS(ls,0,0);
        for (int classIndex1 = 0; classIndex1 < numExamClass - 1; classIndex1++) {
            for (int classIndex2 = classIndex1 + 1; classIndex2 < numExamClass; classIndex2++) {
                // Same room, same day ==> not same timeslot
                S.post(new Implicate(
                        new AND(
                                new IsEqual(examRooms[classIndex1], examRooms[classIndex2]),
                                new IsEqual(examDays[classIndex1], examDays[classIndex2])
                        ),
                        new NotEqual(examTimeSlots[classIndex1], examTimeSlots[classIndex2])
                ));
                
                // Same timeslot, same day ==> not any same teacher
                S.post(new Implicate(
                        new AND(
                                new IsEqual(examTimeSlots[classIndex1], examTimeSlots[classIndex2]),
                                new IsEqual(examDays[classIndex1], examDays[classIndex2])
                        ),
                        new AND(
                                new NotEqual(
                                        examClassToTeacher[classIndex1][1],
                                        examClassToTeacher[classIndex2][0]
                                ),
                                new AND(
                                        new NotEqual(
                                                examClassToTeacher[classIndex1][1],
                                                examClassToTeacher[classIndex2][1]
                                        ),
                                        new AND(
                                                new NotEqual(
                                                        examClassToTeacher[classIndex1][0],
                                                        examClassToTeacher[classIndex2][0]
                                                ),
                                                new NotEqual(
                                                        examClassToTeacher[classIndex1][0],
                                                        examClassToTeacher[classIndex2][1]
                                                ))
                                )
                        )
                ));
                
  
                // Same student ==> not same timeslot, not same day
                convertIntToVarIntLS[1] = new VarIntLS(ls,commonStudents[classIndex1][classIndex2],commonStudents[classIndex1][classIndex2]);
                S.post(
                new Implicate(
                        new LessThan(convertIntToVarIntLS[0], convertIntToVarIntLS[1]),
                        new AND(
                                new NotEqual(examTimeSlots[classIndex1], examTimeSlots[classIndex2]),
                                new NotEqual(examDays[classIndex1], examDays[classIndex2])
                        )
                )
                );
                
                
                
   
            }
        }
        
        
        
        
        
        
        
        
        
              
        
        

    }

    public static void main(String[] args) {
        System.out.println("");
    }

}
