package com.english.model;

import java.io.Serializable;

public class Room implements Serializable {
    private String roomId;
    private String roomName;
    private String centerId;
    private int capacity;
    private boolean available;

    public Room() {
    }

    public Room(String roomId, String roomName, String centerId, int capacity, boolean available) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.centerId = centerId;
        this.capacity = capacity;
        this.available = available;
    }

//Getter
    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getCenterId() {
        return centerId;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAvailable() {
        return available;
    }

//Setter
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
