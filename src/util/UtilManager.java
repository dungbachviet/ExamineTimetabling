/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import model.Area;
import model.Course;
import model.ExamClass;
import model.ExamineTimetablingManager;
import model.Room;
import model.Teacher;
import model.TimeUnit;

/**
 *
 * @author quancq
 */
public class UtilManager {

    public static ExamineTimetablingManager generateData(int difficultLevelOfData) {

        // parameters control difficult level
        int numAreas = 3;
        int numRooms = numAreas * 3;
        int numCourses = 100;
        int numExamClasses = numCourses * 2;
        int numExamDays = 21;
        int numStudents = 1000;
        int numTeachers = 50;

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
                return generateDataLevel1(numAreas, numRooms, numCourses, numExamClasses, numExamDays, numStudents, numTeachers);
            }
            default:
                return generateDataLevel1(numAreas, numRooms, numCourses, numExamClasses, numExamDays, numStudents, numTeachers);
        }
    }

    public static ExamineTimetablingManager generateDataLevel1(
            int numAreas,
            int numRooms,
            int numCourses,
            int numExamClasses,
            int numExamDays,
            int numStudents,
            int numTeachers
    ) {

        // parameter corresponding with difficult of dataset
        
//        int minStudentOfClass = 30;
//        int maxStudentOfClass = 90;
//
//        int minNumCoursesOfStudent = 5;
//        int maxNumCoursesOfStudent = 8;
        int minNumSlotsOfRoom = 50;
        int maxNumSlotsOfRoom = 150;

        int minBusyTime = 3;
        int maxBusyTime = 5;

        int minDifficultCourse = 1;
        int maxDifficultCourse = 3;

        int numTabuDays = 3;

        ExamineTimetablingManager etm = new ExamineTimetablingManager();

        HashMap<String, Area> hmIDToArea = new HashMap<>();
        HashMap<String, Course> hmIDToCourse = new HashMap<>();
        HashMap<String, ExamClass> hmIDToExamClass = new HashMap<>();
        HashMap<String, Room> hmIDToRoom = new HashMap<>();
        HashMap<String, Teacher> hmIDToTeacher = new HashMap<>();

        ArrayList<Integer> availableDayList = new ArrayList<>();
        for (int i = 0; i < numExamDays; ++i) {
            availableDayList.add(i);
        }
        for (int i = 0; i < numTabuDays; ++i) {
            int indexRemove = randomInt(0, availableDayList.size() - 1);
            availableDayList.remove(indexRemove);
        }

        ArrayList<Integer> jamLevelList = new ArrayList<>();
        jamLevelList.add(4);        // mức độ tắc nghẽn của kíp 0 là mức 4 - mức cao nhất
        jamLevelList.add(2);
        jamLevelList.add(1);
        jamLevelList.add(3);

        Random R = new Random(7);

        // generate rooms and areas
        for (int i = 0; i < numAreas; ++i) {
            String areaID = "Area" + i;
            Area area = new Area(areaID);
            hmIDToArea.put(areaID, area);
        }

        for (int i = 0; i < numRooms; ++i) {
            String roomID = "Room" + i;
            String areaID = "Area" + (i % numAreas);
            Area area = hmIDToArea.get(areaID);

            int numSlots = randomInt(minNumSlotsOfRoom, maxNumSlotsOfRoom);
            int numBusyTime = randomInt(minBusyTime, maxBusyTime);
            HashSet<TimeUnit> hsTimeUnit = new HashSet<>();

            while (hsTimeUnit.size() < numBusyTime) {
                TimeUnit timeUnit = new TimeUnit(randomInt(0, numExamDays-1), randomInt(0, 3));
                hsTimeUnit.add(timeUnit);
            }
            ArrayList<TimeUnit> busyTimeList = new ArrayList<>(hsTimeUnit);
            Room room = new Room(roomID, area, numSlots, busyTimeList);
            hmIDToRoom.put(roomID, room);

            area.addRoom(room);
        }

        //generate course - teacher - exam class
        // generate teachers
        for (int i = 0; i < numTeachers; ++i) {
            String teacherID = "Teacher" + i;
            int numBusyTime = randomInt(minBusyTime, maxBusyTime);
            HashSet<TimeUnit> hsTimeUnit = new HashSet<>();

            while (hsTimeUnit.size() < numBusyTime) {
                TimeUnit timeUnit = new TimeUnit(randomInt(0, numExamDays-1), randomInt(0, 3));
                hsTimeUnit.add(timeUnit);
            }
            ArrayList<TimeUnit> busyTimeList = new ArrayList<>(hsTimeUnit);

            Teacher teacher = new Teacher(teacherID, busyTimeList);
            hmIDToTeacher.put(teacherID, teacher);
        }

        // generate courses
        for (int i = 0; i < numCourses; ++i) {
            String courseID = "Course" + i;
            int difficultOfCourse = randomInt(minDifficultCourse, maxDifficultCourse);
            Course course = new Course(courseID, difficultOfCourse);
            hmIDToCourse.put(courseID, course);

            // add connection between course and teacher
            // each course taught by 2 teacher
            for (int j = 0; j < 2; ++j) {
                String teacherID = "Teacher" + String.valueOf(randomInt(0, numTeachers-1));
                Teacher teacher = hmIDToTeacher.get(teacherID);
                teacher.addCourse(course);
                course.addTeacher(teacher);
            }
        }

        // generate exam classes
        for (int i = 0; i < numExamClasses; ++i) {
            String examClassID = "ExamClass" + i;
            ExamClass examClass = new ExamClass(examClassID);
            hmIDToExamClass.put(examClassID, examClass);

            // add connection between course and exam class
            String courseID = "Course" + String.valueOf(randomInt(0, numCourses-1));
            Course course = hmIDToCourse.get(courseID);
            course.addExamClass(examClass);
            examClass.setCourse(course);
        }

        // generate student enrollment
        for (int i = 0; i < numStudents; ++i) {
            String studentID = "Student" + i;
            String courseID = "Course" + String.valueOf(randomInt(0, numCourses-1));
            Course course = hmIDToCourse.get(courseID);
            ArrayList<ExamClass> examClassList = course.getExamClassList();
            if (!examClassList.isEmpty()) {
                ExamClass examClass = examClassList.get(randomInt(0, examClassList.size() - 1));
                // add connection student enrollment exam class
                examClass.addEnrollment(studentID);
            }
        }

        etm.setHmIDToArea(hmIDToArea);
        etm.setHmIDToCourse(hmIDToCourse);
        etm.setHmIDToExamClass(hmIDToExamClass);
        etm.setHmIDToRoom(hmIDToRoom);
        etm.setHmIDToTeacher(hmIDToTeacher);
        etm.setHmIDToTeacher(hmIDToTeacher);
        etm.setAvailableDayList(availableDayList);
        etm.setJamLevelList(jamLevelList);

        return etm;
    }

    public static int randomInt(int min, int max) {
        Random R = new Random();
        return R.nextInt(max - min + 1) + min;
    }

    public static void main(String[] args) {
        ExamineTimetablingManager etm = UtilManager.generateData(1);
        System.out.println(etm);

        System.out.println("\n==========================================\n");

        String path = "src/dataset_timetabling/test_data.txt";
        DataIO.writeObject(path, etm);

        ExamineTimetablingManager etm2 = DataIO.readObject(path);

        System.out.println("\nETM read from file:\n");
        System.out.println(etm2);
    }
}
