package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.StudentAvailability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentAvailabilityDAO {
    public boolean insertStudentAvailability(StudentAvailability studentAvailability) {
        String query = "INSERT INTO mentor_availability (availability_id, student_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentAvailability.getAvailabilityId());
            statement.setString(2, studentAvailability.getStudentId());
            statement.setString(3, studentAvailability.getDayOfWeek().toString());
            statement.setTime(4, java.sql.Time.valueOf(studentAvailability.getStartTime()));
            statement.setTime(5, java.sql.Time.valueOf(studentAvailability.getEndTime()));

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateMentorAvailability(StudentAvailability studentAvailability) {
        String query = "UPDATE mentor_availability SET student_id=?, day_of_week=?, start_time=?, end_time=? WHERE availability_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentAvailability.getStudentId());
            statement.setString(2, studentAvailability.getDayOfWeek().toString());
            statement.setTime(4, java.sql.Time.valueOf(studentAvailability.getStartTime()));
            statement.setTime(5, java.sql.Time.valueOf(studentAvailability.getEndTime()));
            statement.setString(3, studentAvailability.getAvailabilityId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteMentorAvailability(StudentAvailability mentorAvailability) {
        String query = "DELETE FROM mentor_availability WHERE availability_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentorAvailability.getAvailabilityId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
