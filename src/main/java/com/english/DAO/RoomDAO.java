package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public boolean insertRoom(Room room) {
        String query = "INSERT INTO room (room_id, room_name, center_id, capacity, is_available) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, room.getRoomId());
            statement.setString(2, room.getRoomName());
            statement.setString(3, room.getCenterId());
            statement.setInt(4, room.getCapacity());
            statement.setBoolean(5, room.isAvailable());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateRoom(Room room) {
        String query = "UPDATE room SET room_name=?, center_id=?, capacity=?, is_available=? WHERE room_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, room.getRoomName());
            statement.setString(2, room.getCenterId());
            statement.setInt(3, room.getCapacity());
            statement.setBoolean(4, room.isAvailable());
            statement.setString(5, room.getRoomId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteRoom(Room room) {
        String query = "DELETE FROM room WHERE room_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, room.getRoomId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Room getRoomById(String roomId) {
        String query = "SELECT * FROM room WHERE room_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, roomId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Room(
                    rs.getString("room_id"),
                    rs.getString("room_name"),
                    rs.getString("center_id"),
                    rs.getInt("capacity"),
                    rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Room> getAllRooms() {
        String query = "SELECT * FROM room";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while(rs.next()) {
                rooms.add(new Room(
                        rs.getString("room_id"),
                        rs.getString("room_name"),
                        rs.getString("center_id"),
                        rs.getInt("capacity"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getRoomByCenter(String centerId) {
        String query = "SELECT * FROM room WHERE center_id=?";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, centerId);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                rooms.add(new Room(
                        rs.getString("room_id"),
                        rs.getString("room_name"),
                        rs.getString("center_id"),
                        rs.getInt("capacity"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getAvailableRooms() {
        String query = "SELECT * FROM room WHERE is_available=1";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                rooms.add(new Room(
                        rs.getString("room_id"),
                        rs.getString("room_name"),
                        rs.getString("center_id"),
                        rs.getInt("capacity"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }
}
