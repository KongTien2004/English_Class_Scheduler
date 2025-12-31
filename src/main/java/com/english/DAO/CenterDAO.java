package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.Center;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CenterDAO {
    public boolean insertCenter(Center center) {
        String query = "INSERT INTO center (center_id, center_name, address) VALUES (?, ?, ?)";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, center.getCenterId());
            statement.setString(2, center.getCenterName());
            statement.setString(3, center.getAddress());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCenter(Center center) {
        String query = "UPDATE center SET center_name=?, address=? WHERE center_id=?";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, center.getCenterName());
            statement.setString(2, center.getAddress());
            statement.setString(3, center.getCenterId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCenter(String centerId) {
        String query = "DELETE FROM center WHERE center_id=?";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, centerId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Center> getAllCenters() {
        String query = "SELECT * FROM center";
        List<Center> centers = new ArrayList<>();
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                centers.add(new Center(
                    rs.getString("center_id"),
                    rs.getString("center_name"),
                    rs.getString("address"),
                    rs.getString("city")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centers;
    }

    public Center getCenterById(String centerId) {
        String query = "SELECT * FROM center WHERE center_id=?";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, centerId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Center(
                    rs.getString("center_id"),
                    rs.getString("center_name"),
                    rs.getString("address"),
                    rs.getString("city")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Center> getCenterByName(String centerName) {
        String query = "SELECT * FROM center WHERE center_name LIKE ?";
        List<Center> centers = new ArrayList<>();
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + centerName + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                centers.add(new Center(
                    rs.getString("center_id"),
                    rs.getString("center_name"),
                    rs.getString("address"),
                    rs.getString("city")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centers;
    }

    public List<Center> getCenterByAddress(String address) {
        String query = "SELECT * FROM center WHERE address LIKE ?";
        List<Center> centers = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + address + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                centers.add(new Center(
                        rs.getString("center_id"),
                        rs.getString("center_name"),
                        rs.getString("address"),
                        rs.getString("city")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return centers;
    }
}
