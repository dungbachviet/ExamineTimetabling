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
        int numCourses = 6;

        int numExamClasses = (int)(numCourses * 1.5);
        int numExamDays = 10;
        int numStudents = 100;
        int numTeachers = 6;

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
        int maxStudentOfClass = 75;
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

        int minCourseOfStudent = 3;
        int maxCourseOfStudent = 6;

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
        jamLevelList.add(1);        // mức độ tắc nghẽn của kíp 0 là mức 4 - mức cao nhất
        jamLevelList.add(3);
        jamLevelList.add(4);
        jamLevelList.add(2);

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
                TimeUnit timeUnit = new TimeUnit(randomInt(0, numExamDays - 1), randomInt(0, 3));
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
                TimeUnit timeUnit = new TimeUnit(randomInt(0, numExamDays - 1), randomInt(0, 3));
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
            ArrayList<String> addedTeacherID = new ArrayList<>();
            for (int j = 0; j < 2; ++j) {
                String teacherID = "Teacher" + String.valueOf(randomInt(0, numTeachers - 1));
                while (addedTeacherID.contains(teacherID)) {
                    teacherID = "Teacher" + String.valueOf(randomInt(0, numTeachers - 1));
                }
                addedTeacherID.add(teacherID);
                Teacher teacher = hmIDToTeacher.get(teacherID);
                teacher.addCourse(course);
                course.addTeacher(teacher);
            }
        }

        // generate exam classes
        int idCourse = 0;
        for (int i = 0; i < numExamClasses; ++i) {
            String examClassID = "ExamClass" + i;
            ExamClass examClass = new ExamClass(examClassID);
            hmIDToExamClass.put(examClassID, examClass);

            // add connection between course and exam class
            String courseID = "Course" + idCourse;

            Course course = hmIDToCourse.get(courseID);
            idCourse = (idCourse + 1) % (numCourses - 1);

            course.addExamClass(examClass);
            examClass.setCourse(course);
        }

        // generate student enrollment
        for (int i = 0; i < numStudents; ++i) {
            String studentID = "Student" + i;

            // random course for each student
            int numCourseOfStudent = randomInt(minCourseOfStudent, maxCourseOfStudent);
            idCourse = 0;
            int idExamClass = 0;
            for (int j = 0; j < numCourseOfStudent; ++j) {
//                String courseID = "Course" + String.valueOf(randomInt(0, numCourses - 1));
                boolean loop = true;
                while (loop) {
                    String courseID = "Course" + idCourse;
                    Course course = hmIDToCourse.get(courseID);
                    ExamClass examClass = course.getExamClassEmptyEnrollment();

                    if (examClass == null) {
                        ArrayList<ExamClass> examClassList = course.getExamClassList();
                        if (!examClassList.isEmpty()) {
//                        idExamClass = (idExamClass + 1) * (examClassList.size() - 1);
                            examClass = examClassList.get(randomInt(0, examClassList.size() - 1));
                            // add connection student enrollment exam class
                            if (examClass.getEnrollmentList().size() >= maxStudentOfClass) {
                                loop = true;
                            } else {
                                examClass.addEnrollment(studentID);
                                loop = false;
                            }
                        } else {
                            loop = true;
                        }
                    } else {
                        loop = false;
                        examClass.addEnrollment(studentID);
                    }

                    idCourse = (idCourse + 1) % (numCourses - 1);
                }
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

    public static ArrayList<Integer> randomIntegerList(int size, int min, int max) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            list.add(randomInt(min, max));
        }
        return list;
    }

    public static double randomDouble(int min, int max) {
        Random R = new Random();
        return (max - min + 1) * R.nextDouble() + min;
    }

    public static ArrayList<Double> randomDoubleList(int size, int min, int max) {
        ArrayList<Double> list = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            list.add(randomDouble(min, max));
        }
        return list;
    }

    public static HashMap<String, Double> createHashMapFromList(ArrayList<String> list, double[] arr) {
        HashMap<String, Double> hm = new HashMap<>();

        for (int i = 0; i < arr.length; ++i) {
            hm.put(list.get(i), arr[i]);
        }

        return hm;
    }

    public static double getMin(ArrayList<Double> list) {
        double min = Double.MAX_VALUE;
        for (double v : list) {
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    public static double getMax(ArrayList<Double> list) {
        double max = Double.MIN_VALUE;
        for (double v : list) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    public static double getAverage(ArrayList<Double> list) {
        double sum = 0;
        for (double v : list) {
            sum += v;
        }
        return sum / list.size();
    }

    public static double getVariance(ArrayList<Double> list) {
        double sum = 0;
        double avg = getAverage(list);
        for (double v : list) {
            sum += Math.abs(v - avg);
        }
        return sum / list.size();
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
