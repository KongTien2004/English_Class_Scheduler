package com.english.controller;

import com.english.model.Room;
import com.english.service.RoomService;

import javax.swing.*;
import java.util.List;

public class RoomController {
    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public void insertRoom(Room room, JFrame frame) {
        if (roomService.insertRoom(room)) {
            JOptionPane.showMessageDialog(frame,
                    "Room inserted successfully!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateRoom(Room room, JFrame frame) {
        if (roomService.updateRoom(room)) {
            JOptionPane.showMessageDialog(frame,
                    "Room has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating room failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteRoom(Room room, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are your sure to delete this room?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (roomService.deleteRoom(room)) {
                JOptionPane.showMessageDialog(frame,
                        "Room has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting room failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Room getRoomById(String roomId) {
        return roomService.getRoomById(roomId);
    }

    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    public List<Room> getRoomByCenter (String centerId) {
        return roomService.getRoomByCenter(centerId);
    }

    public int totalRooms() {
        return roomService.totalRooms();
    }
}
