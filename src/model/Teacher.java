/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author quancq
 */
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;
    private String teacherID;
    private ArrayList<Course> teachingCourseList;
    private ArrayList<TimeUnit> busyTimeList;

    public Teacher(String teacherID, ArrayList<TimeUnit> busyTimeList) {
        this.teacherID = teacherID;
        this.busyTimeList = busyTimeList;
        this.teachingCourseList = new ArrayList<>();
    }

    public Teacher(String teacherID, ArrayList<Course> teachingCourseList, ArrayList<TimeUnit> busyTimeList) {
        this.teacherID = teacherID;
        this.teachingCourseList = teachingCourseList;
        this.busyTimeList = busyTimeList;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public int getTeacherIDInt() {
        String id = teacherID.substring("Teacher".length());
        return Integer.parseInt(id);
    }

    public ArrayList<Course> getTeachingCourseList() {
        return teachingCourseList;
    }

    public ArrayList<TimeUnit> getBusyTimeList() {
        return busyTimeList;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public void setTeachingCourseList(ArrayList<Course> teachingCourseList) {
        this.teachingCourseList = teachingCourseList;
    }

    public void setBusyTimeList(ArrayList<TimeUnit> busyTimeList) {
        this.busyTimeList = busyTimeList;
    }

    public void addCourse(Course course) {
        teachingCourseList.add(course);
    }

    public void addBusyTime(TimeUnit busyTime) {
        busyTimeList.add(busyTime);
    }

    @Override
    public String toString() {
        return "Teacher{" + "teacherID=" + teacherID + ", teachingCourseList=" + teachingCourseList + ", busyTimeList=" + busyTimeList + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.teacherID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Teacher other = (Teacher) obj;
        return this.teacherID.equals(other.teacherID);
    }

}
