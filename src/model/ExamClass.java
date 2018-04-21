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
public class ExamClass implements Serializable {

    private static final long serialVersionUID = 1L;
    private String examClassID;
    private Course course;
    private ArrayList<String> enrollmentList;

    public ExamClass(String examClassID) {
        this.examClassID = examClassID;
        this.enrollmentList = new ArrayList<>();
    }

    public ExamClass(String examClassID, Course course, ArrayList<String> enrollmentList) {
        this.examClassID = examClassID;
        this.course = course;
        this.enrollmentList = enrollmentList;
    }

    public String getExamClassID() {
        return examClassID;
    }

    public int getExamClassIDInt() {
        String id = examClassID.substring("ExamClass".length());
        return Integer.parseInt(id);
    }

    public Course getCourse() {
        return course;
    }

    public ArrayList<String> getEnrollmentList() {
        return enrollmentList;
    }

    public void setExamClassID(String examClassID) {
        this.examClassID = examClassID;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setEnrollmentList(ArrayList<String> enrollmentList) {
        this.enrollmentList = enrollmentList;
    }

    public void addEnrollment(String studentID) {
        if (!enrollmentList.contains(studentID)) {
            enrollmentList.add(studentID);
        }
    }

    @Override
    public String toString() {
        return "ExamClass{" + "examClassID=" + examClassID + ", course=" + course.getCourseID() + ", enrollmentList=" + enrollmentList + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.examClassID);
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
        final ExamClass other = (ExamClass) obj;
        return this.examClassID.equals(other.examClassID);
    }

}
