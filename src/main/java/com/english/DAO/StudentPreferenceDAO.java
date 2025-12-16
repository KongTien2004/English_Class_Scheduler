package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.StudentPreference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentPreferenceDAO {
    public boolean insertStudentPreference(StudentPreference studentPreference) {
        String query = "INSERT INTO student_preference (preference_id, student_id, preferred_center, day_of_week, preferred_start, preferred_end) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, studentPreference.getPreferenceId());
            statement.setString(2, studentPreference.getStudentId());
            statement.setString(3, studentPreference.getPreferredCenter());
            statement.setString(4, studentPreference.getDayOfWeek().toString());
            statement.setTime(5, java.sql.Time.valueOf(studentPreference.getPreferredStart()));
            statement.setTime(6, java.sql.Time.valueOf(studentPreference.getPreferredEnd()));

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateStudentPreference(StudentPreference studentPreference) {
        String query = "UPDATE student_preference SET student_id=?, preferred_center=?, day_of_week=?, preferred_start=?, preferred_end=? WHERE preference_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentPreference.getStudentId());
            statement.setString(2, studentPreference.getPreferredCenter());
            statement.setString(3, studentPreference.getDayOfWeek().toString());
            statement.setTime(4, java.sql.Time.valueOf(studentPreference.getPreferredStart()));
            statement.setTime(5, java.sql.Time.valueOf(studentPreference.getPreferredEnd()));
            statement.setString(6, studentPreference.getPreferenceId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteStudentPreference(StudentPreference studentPreference) {
        String query = "DELETE FROM student_preference WHERE preference_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentPreference.getPreferenceId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public StudentPreference getStudentPreferenceById(String preferenceId) {
        String query = "SELECT * FROM student_preference WHERE preference_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, preferenceId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new StudentPreference(
                        rs.getString("preference_id"),
                        rs.getString("student_id"),
                        rs.getString("preferred_center"),
                        StudentPreference.DayOfWeeks.valueOf(rs.getString("day_of_week")),
                        rs.getTime("preferred_start").toLocalTime(),
                        rs.getTime("preferred_end").toLocalTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<StudentPreference> getAllPreferences() {
        String query = "SELECT * FROM student_preference";
        List<StudentPreference> preferences = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                preferences.add(new StudentPreference(
                        rs.getString("preference_id"),
                        rs.getString("student_id"),
                        rs.getString("preferred_center"),
                        StudentPreference.DayOfWeeks.valueOf(rs.getString("day_of_week")),
                        rs.getTime("preferred_start").toLocalTime(),
                        rs.getTime("preferred_end").toLocalTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preferences;
    }

    public List<StudentPreference> getPreferenceByStudent(String studentId) {
        String query = "SELECT * FROM student_preference WHERE student_id=?";
        List<StudentPreference> studentPreferences = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                studentPreferences.add(new StudentPreference(
                        rs.getString("preference_id"),
                        rs.getString("student_id"),
                        rs.getString("preferred_center"),
                        StudentPreference.DayOfWeeks.valueOf(rs.getString("day_of_week")),
                        rs.getTime("preferred_start").toLocalTime(),
                        rs.getTime("preferred_end").toLocalTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentPreferences;
    }
}
