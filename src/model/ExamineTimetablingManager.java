/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    private final int NUM_TIMESLOTS_PER_DAY = 4;

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

    public Set<Integer> getAvailableDaySet() {
        Set<Integer> set = new HashSet<>(availableDayList);
        return set;
    }

    public ArrayList<Integer> getJamLevelList() {
        return jamLevelList;
    }

    public int getNumAreas() {
        return hmIDToArea.size();
    }

    public int getNumCourses() {
        return hmIDToCourse.size();
    }

    public int getNumExamClasses() {
        return hmIDToExamClass.size();
    }

    public int getNumRooms() {
        return hmIDToRoom.size();
    }

    public int getNumTeachers() {
        return hmIDToTeacher.size();
    }

    public int[] getRoomSlots() {
        ArrayList<Room> roomList = getRoomList();
        int[] result = new int[roomList.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = roomList.get(i).getNumSlots();
        }
        return result;
    }

    public String getDatasetName() {
        String name = "Data-" + getNumAreas() + "-"
                + getNumCourses() + "-"
                + getNumExamClasses() + "-"
                + getNumRooms() + "-"
                + getNumTeachers();
        return name;
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\n============= <ETM> =============");

        str.append("\nList available days: " + availableDayList.size() + "\n");
        str.append(availableDayList);

        str.append("\nList jam level: " + jamLevelList.size() + "\n");
        str.append(jamLevelList);

        str.append("\nList areas: " + hmIDToArea.size() + "\n");
        for (String id : hmIDToArea.keySet()) {
            Area area = hmIDToArea.get(id);
            str.append(area + "\n");
        }

        str.append("\nList courses: " + hmIDToCourse.size() + "\n");
        for (String id : hmIDToCourse.keySet()) {
            Course course = hmIDToCourse.get(id);
            str.append(course + "\n");
        }

        str.append("\nList exam class: " + hmIDToExamClass.size() + "\n");
        for (String id : hmIDToExamClass.keySet()) {
            ExamClass examClass = hmIDToExamClass.get(id);
            str.append(examClass + "\n");
        }

        str.append("\nList rooms: " + hmIDToRoom.size() + "\n");
        for (String id : hmIDToRoom.keySet()) {
            Room room = hmIDToRoom.get(id);
            str.append(room + "\n");
        }

        str.append("\nList teachers: " + hmIDToTeacher.size() + "\n");
        for (String id : hmIDToTeacher.keySet()) {
            Teacher teacher = hmIDToTeacher.get(id);
            str.append(teacher + "\n");
        }

        str.append("\n============= </ETM> =============");

        return str.toString();
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
                for (String studentID : enrollmentList1) {
                    if (enrollmentList2.contains(studentID)) {
                        numCommonStudents++;
                    }
                }

                result[i][j] = numCommonStudents;
                result[j][i] = result[i][j];
            }
        }

        return result;
    }

    /**
     *
     * @return 1 ArrayList gồm các ArrayList. Mỗi ArrayList con chứa mã index
     * của các lớp thi cùng thuộc 1 học phần
     */
    public ArrayList<ArrayList<Integer>> getCommonExamClassCourseList() {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        for (Course course : getCourseList()) {
            ArrayList<Integer> list = new ArrayList<>();
            for (ExamClass examClass : course.getExamClassList()) {
                list.add(examClass.getExamClassIDInt());
            }
        }

        return result;
    }

    private int getStartDay() {
        int startDay = Integer.MAX_VALUE;
        for (Integer day : availableDayList) {
            if (day < startDay) {
                startDay = day;
            }
        }
        return startDay;
    }

    private int getEndDay() {
        int endDay = Integer.MIN_VALUE;
        for (Integer day : availableDayList) {
            if (day > endDay) {
                endDay = day;
            }
        }
        return endDay;
    }

    /**
     *
     * @return Threshold of maximum the minimum of gap between 2 exam times
     */
    public int getGapThreshold() {
        int threshold = 0;

        // calculate range of time slot
        int startDay = getStartDay();
        int endDay = getEndDay();
        int rangeOfTimeSlot = (endDay - startDay + 1) * NUM_TIMESLOTS_PER_DAY;

        // find max number exam of one student
        int maxNumExamClassesOfStudent = 0;
        ArrayList<ExamClass> examClassList = getExamClassList();
        HashMap<String, Integer> hmStudentToNumExam = new HashMap<>();
        for (ExamClass exam : examClassList) {
            for (String studentID : exam.getEnrollmentList()) {
                Integer numExam = hmStudentToNumExam.get(studentID);
                if (numExam == null) {
                    hmStudentToNumExam.put(studentID, 1);
                } else {
                    hmStudentToNumExam.put(studentID, numExam + 1);
                }
            }
        }
        for (String studentID : hmStudentToNumExam.keySet()) {
            int numExam = hmStudentToNumExam.get(studentID);
            if (maxNumExamClassesOfStudent < numExam) {
                maxNumExamClassesOfStudent = numExam;
            }
        }

        threshold = (int) (Math.ceil(rangeOfTimeSlot / maxNumExamClassesOfStudent));
        return threshold;
    }

    /**
     *
     * @return Threshold of difficult courses should be examed nearly at last of
     * exam process
     */
    public int getDifficultExamThreshold() {
        int threshold = 0;
        int endDay = getEndDay();
        int sumDifficultExam = 0;
        for (Course course : getCourseList()) {
            sumDifficultExam += course.getNumExamClasses() * course.getDifficultLevel();
        }

        threshold = sumDifficultExam * endDay * NUM_TIMESLOTS_PER_DAY;
        return threshold;
    }

    /**
     *
     * @return Threshold of disproportination between exam class's number of
     * student and room's capacity
     */
    public int getDisproportinationThreshold() {
        int threshold = 0;

        // sort room list in ascending order of capacity
        ArrayList<Room> roomList = getRoomList();
        Comparator ascendingRoomCapacity = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Room r1 = (Room) o1;
                Room r2 = (Room) o2;
                return Integer.compare(r1.getNumSlots(), r2.getNumSlots());
            }
        };
        Collections.sort(roomList, ascendingRoomCapacity);

        for (ExamClass exam : getExamClassList()) {
            int deviation = 0;
            for (Room room : roomList) {
                deviation = room.getNumSlots() - exam.getNumStudentEnroll();
                if (deviation >= 0) {
                    if (deviation > threshold) {
                        threshold = deviation;
                    }
                    break;
                }
            }

        }

        return threshold;
    }

    /**
     *
     * @return Threshold of traffic jam of time slot and its number exam class
     */
    public int getTrafficJamThreshold() {
        int threshold = 0;

        int numExamClass = hmIDToExamClass.size();
        int maxTrafficJamLevel = Integer.MIN_VALUE;

        for (Integer level : jamLevelList) {
            if (level > maxTrafficJamLevel) {
                maxTrafficJamLevel = level;
            }
        }

        threshold = numExamClass * maxTrafficJamLevel;
        return threshold;
    }
}
