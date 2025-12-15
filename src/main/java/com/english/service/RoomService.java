package com.english.service;

import com.english.DAO.RoomDAO;
import com.english.model.Room;

import java.util.List;

public class RoomService {
    private RoomDAO roomDAO;

    public RoomService(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    public boolean insertRoom(Room room) {
        if (!validateRoom(room)) return false;
        return roomDAO.insertRoom(room);
    }

    public boolean updateRoom(Room room) {
        if (!validateRoom(room)) return false;
        return roomDAO.updateRoom(room);
    }

    public boolean deleteRoom(Room room) {
        if (!validateRoom(room)) return false;
        return roomDAO.deleteRoom(room);
    }

    public Room getRoomById(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) return null;
        return roomDAO.getRoomById(roomId);
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    private boolean validateRoom(Room room) {
        if (room == null) return false;
        if (room.getRoomId() == null || room.getRoomId().isEmpty()) return false;
        if (room.getRoomName() == null || room.getRoomName().isEmpty()) return false;
        if (room.getCenterId() == null || room.getCenterId().isEmpty()) return false;
        if (room.getCapacity() <= 0) return false;
        return true;
    }

    public int totalRooms() {
        return roomDAO.getAllRooms().size();
    }
}
