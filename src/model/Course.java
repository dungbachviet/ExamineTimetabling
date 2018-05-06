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
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;
    private String courseID;
    private int difficultLevel;
    private ArrayList<ExamClass> examClassList;
    private ArrayList<Teacher> teacherList;

    public Course(String courseID, int difficultLevel) {
        this.courseID = courseID;
        this.difficultLevel = difficultLevel;
        this.examClassList = new ArrayList<>();
        this.teacherList = new ArrayList<>();
    }

    public String getCourseID() {
        return courseID;
    }

    public int getCourseIDInt() {
        String id = courseID.substring("Course".length());
        return Integer.parseInt(id);
    }

    public int getDifficultLevel() {
        return difficultLevel;
    }

    public ArrayList<ExamClass> getExamClassList() {
        return examClassList;
    }
    
    public int getNumExamClasses(){
        return examClassList.size();
    }

    public ArrayList<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setDifficultLevel(int difficultLevel) {
        this.difficultLevel = difficultLevel;
    }

    public void setExamClassList(ArrayList<ExamClass> examClassList) {
        this.examClassList = examClassList;
    }

    public void setTeacherList(ArrayList<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    public void addExamClass(ExamClass examClass) {
        examClassList.add(examClass);
    }

    public void addTeacher(Teacher teacher) {
        teacherList.add(teacher);
    }

    @Override
    public String toString() {
        return "Course{" + "ID=" + courseID + ", difficult=" + difficultLevel + '}';
    }

    public ExamClass getExamClassEmptyEnrollment(){
        ExamClass result = null;
        
        for(ExamClass examClass : examClassList){
            if(examClass.getEnrollmentList().isEmpty()){
                result = examClass;
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.courseID);
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
        final Course other = (Course) obj;
        return this.courseID.equals(other.courseID);
    }
}
