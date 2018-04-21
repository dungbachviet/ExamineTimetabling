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
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;
    private String areaID;
    private ArrayList<Room> roomList;

    public Area(String areaID) {
        this.areaID = areaID;
    }

    public String getAreaID() {
        return areaID;
    }

    public ArrayList<Room> getRoomList() {
        return roomList;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public void setRoomList(ArrayList<Room> roomList) {
        this.roomList = roomList;
    }

    @Override
    public String toString() {
        return "Area{" + "areaID=" + areaID + ", roomList=" + roomList + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.areaID);
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
        final Area other = (Area) obj;
        return this.areaID.equals(other.areaID);
    }

}
