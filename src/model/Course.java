/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author quancq
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;
    private String courseID;
    private int difficultLevel;

    public Course(String courseID, int difficultLevel) {
        this.courseID = courseID;
        this.difficultLevel = difficultLevel;
    }

    public String getCourseID() {
        return courseID;
    }

    public int getDifficultLevel() {
        return difficultLevel;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setDifficultLevel(int difficultLevel) {
        this.difficultLevel = difficultLevel;
    }

    @Override
    public String toString() {
        return "Course{" + "courseID=" + courseID + ", difficultLevel=" + difficultLevel + '}';
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
