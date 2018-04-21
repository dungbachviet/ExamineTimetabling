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
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    private String roomID;
    private Area area;
    private int numSlots;
    private ArrayList<TimeUnit> busyTimeList;

    public Room(String roomID, Area area, int numSlots, ArrayList<TimeUnit> busyTimeList) {
        this.roomID = roomID;
        this.area = area;
        this.numSlots = numSlots;
        this.busyTimeList = busyTimeList;
    }

    public String getRoomID() {
        return roomID;
    }

    public Area getArea() {
        return area;
    }

    public int getNumSlots() {
        return numSlots;
    }

    public ArrayList<TimeUnit> getBusyTimeList() {
        return busyTimeList;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void setNumSlots(int numSlots) {
        this.numSlots = numSlots;
    }

    public void setBusyTimeList(ArrayList<TimeUnit> busyTimeList) {
        this.busyTimeList = busyTimeList;
    }

    public void addBusyTime(TimeUnit busyTime){
        busyTimeList.add(busyTime);
    }
    
    @Override
    public String toString() {
        return "Room{" + "roomID=" + roomID + ", area=" + area.getAreaID() + ", numSlots=" + numSlots + ", busyTimeList=" + busyTimeList + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.roomID);
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
        final Room other = (Room) obj;
        return this.roomID.equals(other.roomID);
    }

}
