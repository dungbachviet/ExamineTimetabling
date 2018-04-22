/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author quancq
 */
public class TimeUnit implements Serializable {

    private static final long serialVersionUID = 1L;
    private int day;
    private int timeSlot;

    public TimeUnit(int day, int timeSlot) {
        this.day = day;
        this.timeSlot = timeSlot;
    }

    public int getDay() {
        return day;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return "TimeUnit{" + day + ", " + timeSlot + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.day;
        hash = 67 * hash + this.timeSlot;
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
        final TimeUnit other = (TimeUnit) obj;
        return this.day == other.day && this.timeSlot == other.timeSlot;
    }
}
