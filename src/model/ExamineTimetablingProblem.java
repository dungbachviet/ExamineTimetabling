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
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.element.Element;
import localsearch.functions.element.ElementTmp;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
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



    public ArrayList<Integer> availableDayList; // ds các ngày có thể tổ chức thi
    public ArrayList<Integer> jamLevelList; // ds mức độ tắc nghẽn của các kíp thi

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
    
    IFunction examGapObj; // Maximize the minimum of gap between 2 exam times
    IFunction consecutiveObj; // Maximize the consecutive time for teacher
    IFunction balanceAreaObj; // Balance rooms at each timeslot for exam areas
    IFunction utilizeRoomObj; // Utilize maximally the capacity of each room, avoid wasting
    IFunction distributeDifficultyObj; // Distribute difficult degree throughout exam process
    IFunction disproportionObj; // Avoid disproportinating between number of student and room's capacity
    IFunction suitableTimeSlotObj; // Avoid helding much exams on traffic jam time
    
    
    public ExamineTimetablingProblem() {
        ExamineTimetablingManager manager = new ExamineTimetablingManager();
        hmIDToArea = manager.getHmIDToArea();
        hmIDToCourse = manager.getHmIDToCourse();
        hmIDToExamClass = manager.getHmIDToExamClass();
        hmIDToRoom = manager.getHmIDToRoom();
        hmIDToTeacher = manager.getHmIDToTeacher();
        
        availableDayList = manager.getAvailableDayList();
        jamLevelList = manager.getJamLevelList();
        
        commonStudents = manager.calcNumberCommonStudentOfClasses();
//        roomSlots = manager.
        numExamClass = manager.getNumExamClasses();
        numTeacher = manager.getNumTeachers();
        numCourse = manager.getNumCourses();
        numRoom = manager.getNumRooms();
        numArea = manager.getNumAreas();

        areas = manager.getAreaList();
        courses = manager.getCourseList();
        examClasses = manager.getExamClassList();
        rooms = manager.getRoomList();
        teachers = manager.getTeacherList();
        setAvailableDay = manager.getAvailableDaySet();
        sameCourseCodeClass = manager.getCommonExamClassCourseList();
        
    
    }
    

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
        // Constraint : Don't allow any 2 exam classes having any same student are at least 1 timeslot apart
        
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
                
  
                // Same student, same day ==> |timeslot1 - timeslot2| >= 2
                VarIntLS[] convertIntToVarIntLS = new VarIntLS[2];
                convertIntToVarIntLS[0] = new VarIntLS(ls, 0, 0);
                convertIntToVarIntLS[1] = new VarIntLS(ls, commonStudents[classIndex1][classIndex2], commonStudents[classIndex1][classIndex2]);
                
                VarIntLS[] timeSlots = new VarIntLS[2];
                timeSlots[0] = examTimeSlots[classIndex1];
                timeSlots[1] = examTimeSlots[classIndex2];
                
                IFunction max = new Max(timeSlots);
                IFunction min = new Min(timeSlots);
                
                S.post(new Implicate(
                        new AND(
                                new LessThan(convertIntToVarIntLS[0], convertIntToVarIntLS[1]),
                                new IsEqual(examDays[classIndex1], examDays[classIndex2])
                        ),
                        new LessOrEqual(2, new FuncMinus(max, min))
                ));
 
            }
        }
        
        
        // Constraint - Don't allow teacher supervise any exam class having same courseID 
        for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
            int courseID = examClasses.get(classIndex).getCourse().getCourseIDInt();
            VarIntLS convertIntToVarIntLS = new VarIntLS(ls, courseID, courseID);
            for (int teacherIndex = 0; teacherIndex < numTeacher; teacherIndex++) {
                ArrayList<Integer> courseList = teachers.get(teacherIndex).getTeachingCourseListIDInt();
                for (int courseIndex = 0; courseIndex < courseList.size(); courseIndex++) {
                    
                    S.post(
                            new Implicate(
                                    new IsEqual(convertIntToVarIntLS, courseList.get(courseIndex)),
                                    new AND(
                                            new NotEqual(examClassToTeacher[classIndex][0], teacherIndex),
                                            new NotEqual(examClassToTeacher[classIndex][1], teacherIndex)
                                    )
                            )
                    );
                      
                }
                        
            }
        }
        
        // Objective 1 : Maximize the minimum of gap between 2 exam times
        ArrayList<IFunction> commonExamGaps = new ArrayList<IFunction>();
        for (int classIndex1 = 0; classIndex1 < numExamClass - 1; classIndex1++) {
            for (int classIndex2 = classIndex1 + 1; classIndex2 < numExamClass; classIndex2++) {
                if (commonStudents[classIndex1][classIndex2] > 0) {
                    IFunction[] examTimes = new IFunction[2];
                    
                    // Calculates Day[i]*4 + Timeslot[j]
                    examTimes[0] = new FuncPlus(new FuncMult(examDays[classIndex1], 4), examTimeSlots[classIndex1]);
                    examTimes[1] = new FuncPlus(new FuncMult(examDays[classIndex2], 4), examTimeSlots[classIndex2]);
                    
                    commonExamGaps.add(new FuncMinus(new Max(examTimes), new Min(examTimes)));
                }
            }
        }
        
        IFunction[] commonExamGapsFunction = new IFunction[commonExamGaps.size()];
        for (int index = 0; index < commonExamGaps.size(); index++)
            commonExamGapsFunction[index] = commonExamGaps.get(index);
        
        examGapObj = new Min(commonExamGapsFunction); // maximize the minimum
        
        // Objective 2 : Avoid disproportinating between number of student and room's capacity
        IFunction[] slotDisproportion = new IFunction[numExamClass];
        for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
            int classSlots = examClasses.get(classIndex).getEnrollmentList().size();
            slotDisproportion[classIndex] = new FuncMinus(new ElementTmp(roomSlots, examRooms[classIndex]), classSlots);
        }
        
        disproportionObj = new Max(slotDisproportion); // minimize the maximum
        
        // Objective 3 : Avoid helding much exams on traffic jam time
        IFunction[] timeSlotSuits = new IFunction[4];
        for (int timeSlotIndex = 0; timeSlotIndex < jamLevelList.size(); timeSlotIndex++) {
            // Number timeslot i * jamLevel[i]
            timeSlotSuits[timeSlotIndex] = new FuncMult(new ConditionalSum(examTimeSlots, timeSlotIndex), jamLevelList.get(timeSlotIndex));
        }
        suitableTimeSlotObj = new Sum(timeSlotSuits); 
        
        // Objective 4 : 
        IFunction[] times = new IFunction[numExamClass];
        IFunction[] timeMultDifficult = new IFunction[numExamClass];
        for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
            times[classIndex] = new FuncPlus(new FuncMult(examDays[classIndex], 4), examTimeSlots[classIndex]);
            int classDifficulty = examClasses.get(classIndex).getCourse().getDifficultLevel();
            timeMultDifficult[classIndex] = new FuncMult(times[classIndex], classDifficulty);
        }
        
        // Distribute difficult degree throughout exam process
        distributeDifficultyObj = new Sum(timeMultDifficult); // Maximize
        
        
         
        
//        IFunction examGapObj; // Maximize the minimum of gap between 2 exam times
//    IFunction consecutiveObj; // Maximize the consecutive time for teacher
//    IFunction balanceAreaObj; // Balance rooms at each timeslot for exam areas
//    IFunction utilizeRoomObj; // Utilize maximally the capacity of each room, avoid wasting
//    IFunction distributeDifficultyObj; // Distribute difficult degree throughout exam process
//    IFunction disproportionObj; // Avoid disproportinating between number of student and room's capacity
//    IFunction suitableSessionObj; // Avoid helding much exams on traffic jam time
        
        
        
        ls.close();
        
    
        
        

    }

    public static void main(String[] args) {
        System.out.println("");
    }

}
