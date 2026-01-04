package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.Assistant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssistantDAO {
    public boolean insertAssistant(Assistant assistant) {
        String query = "INSERT INTO assistant(assistant_id, assistant_name, email, certified_band, strong_listening, strong_reading, strong_writing, strong_speaking, can_support_general, can_support_academic, is_available, assistant_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, assistant.getAssistantId());
            statement.setString(2, assistant.getAssistantName());
            statement.setString(3, assistant.getEmail());
            statement.setDouble(4, assistant.getCertifiedBand());
            statement.setBoolean(5, assistant.isStrongListening());
            statement.setBoolean(6, assistant.isStrongReading());
            statement.setBoolean(7, assistant.isStrongWriting());
            statement.setBoolean(8, assistant.isStrongSpeaking());
            statement.setBoolean(9, assistant.isCanSupportGeneral());
            statement.setBoolean(10, assistant.isCanSupportAcademic());
            statement.setBoolean(11, assistant.isAvailable());
            statement.setString(12, assistant.getAssistantAddress());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateAssistant(Assistant assistant) {
        String query = "UPDATE assistant SET assistant_name=?, email=?, certified_band=?, strong_listening=?, strong_reading=?, strong_writing=?, strong_speaking=?, can_support_general=?, can_support_academic=?, is_available=?, assistant_address=? WHERE assistant_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, assistant.getAssistantName());
            statement.setString(2, assistant.getEmail());
            statement.setDouble(3, assistant.getCertifiedBand());
            statement.setBoolean(4, assistant.isStrongListening());
            statement.setBoolean(5, assistant.isStrongReading());
            statement.setBoolean(6, assistant.isStrongWriting());
            statement.setBoolean(7, assistant.isStrongSpeaking());
            statement.setBoolean(8, assistant.isCanSupportGeneral());
            statement.setBoolean(9, assistant.isCanSupportAcademic());
            statement.setBoolean(10, assistant.isAvailable());
            statement.setString(11, assistant.getAssistantAddress());
            statement.setString(12, assistant.getAssistantId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteAssistant(String assistantId) {
        String query = "DELETE FROM assistant WHERE assistant_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, assistantId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Assistant getAssistantById(String assistantId) {
        String query = "SELECT * FROM assistant WHERE assistant_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, assistantId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Assistant(
                    rs.getString("assistant_id"),
                    rs.getString("assistant_name"),
                    rs.getString("email"),
                    rs.getDouble("certified_band"),
                    rs.getBoolean("strong_listening"),
                    rs.getBoolean("strong_reading"),
                    rs.getBoolean("strong_writing"),
                    rs.getBoolean("strong_speaking"),
                    rs.getBoolean("can_support_general"),
                    rs.getBoolean("can_support_academic"),
                    rs.getBoolean("is_available"),
                    rs.getString("assistant_address")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Assistant> getAllAssistants() {
        String query = "SELECT * FROM assistant";
        List<Assistant> assistants = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                assistants.add(new Assistant(
                    rs.getString("assistant_id"),
                    rs.getString("assistant_name"),
                    rs.getString("email"),
                    rs.getDouble("certified_band"),
                    rs.getBoolean("strong_listening"),
                    rs.getBoolean("strong_reading"),
                    rs.getBoolean("strong_writing"),
                    rs.getBoolean("strong_speaking"),
                    rs.getBoolean("can_support_general"),
                    rs.getBoolean("can_support_academic"),
                    rs.getBoolean("is_available"),
                    rs.getString("assistant_address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assistants;
    }

    public List<Assistant> getAssistantsByAddress(String assistantAddress) {
        String query = "SELECT * FROM assistant WHERE assistant_address = ?";
        List<Assistant> assistants = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, assistantAddress);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                assistants.add(new Assistant(
                    rs.getString("assistant_id"),
                    rs.getString("assistant_name"),
                    rs.getString("email"),
                    rs.getDouble("certified_band"),
                    rs.getBoolean("strong_listening"),
                    rs.getBoolean("strong_reading"),
                    rs.getBoolean("strong_writing"),
                    rs.getBoolean("strong_speaking"),
                    rs.getBoolean("can_support_general"),
                    rs.getBoolean("can_support_academic"),
                    rs.getBoolean("is_available"),
                    rs.getString("assistant_address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assistants;
    }
}
