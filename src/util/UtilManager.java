/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import model.Area;
import model.Course;
import model.ExamClass;
import model.ExamineTimetablingManager;
import model.Room;
import model.Teacher;

/**
 *
 * @author quancq
 */
public class UtilManager {

    public static ExamineTimetablingManager generateData(
            int numExamDays, int numAreas, int numCourses,int numStudents,
            int numExamClasses, int numRooms, int numTeachers, int difficultLevelOfData
    ) {
        System.out.println("\n============= <Generating data> =============");
        System.out.println("\nRandom data Level " + difficultLevelOfData);
        System.out.println("\nNum exam days: " + numExamDays);
        System.out.println("\nNum areas: " + numAreas);
        System.out.println("\nNum courses: " + numCourses);
        System.out.println("\nNum students: " + numStudents);
        System.out.println("\nNum exam classes: " + numExamClasses);
        System.out.println("\nNum rooms: " + numRooms);
        System.out.println("\nNum teachers: " + numTeachers);
        System.out.println("\n============= </Generating data> =============");

        switch (difficultLevelOfData) {
            case 1: {
                return generateDataLevel1(numExamDays, numAreas, numCourses, numStudents,numExamClasses, numRooms, numTeachers);
            }
            default:
                return generateDataLevel1(numExamDays, numAreas, numCourses, numStudents,numExamClasses, numRooms, numTeachers);
        }
    }

    public static ExamineTimetablingManager generateDataLevel1(
            int numExamDays, int numAreas, int numCourses, int numStudents,
            int numExamClasses, int numRooms, int numTeachers
    ) {
        ExamineTimetablingManager etm = new ExamineTimetablingManager();

        HashMap<String, Area> hmIDToArea = new HashMap<>();
        HashMap<String, Course> hmIDToCourse = new HashMap<>();
        HashMap<String, ExamClass> hmIDToExamClass = new HashMap<>();
        HashMap<String, Room> hmIDToRoom = new HashMap<>();
        HashMap<String, Teacher> hmIDToTeacher = new HashMap<>();

        ArrayList<Integer> availableDayList;
        ArrayList<Integer> jamLevelList;

        // generate rooms and areas
        for (int i = 0; i < numAreas; ++i) {
            String areaID = "Area" + (i + 1);
            Area area = new Area(areaID);
            hmIDToArea.put(areaID, area);
        }

        for (int i = 0; i < numRooms; ++i) {
            String roomID = "Room" + (i + 1);
            String areaID = "Area" + (i % numAreas + 1);
            Area area = hmIDToArea.get(areaID);
            
            int numSlots = 0;
            if(i < numRooms / 2){
                numSlots
            }
            Room room = new Room(roomID, area, numSlots, busyTimeList);
        }

        return etm;
    }
}
