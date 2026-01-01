package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.StudentAvailability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentAvailabilityDAO {
    public boolean insertStudentAvailability(StudentAvailability studentAvailability) {
        String query = "INSERT INTO student_availability (availability_id, student_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

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

    public boolean updateStudentAvailability(StudentAvailability studentAvailability) {
        String query = "UPDATE student_availability SET student_id=?, day_of_week=?, start_time=?, end_time=? WHERE availability_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentAvailability.getStudentId());
            statement.setString(2, studentAvailability.getDayOfWeek().toString());
            statement.setTime(3, java.sql.Time.valueOf(studentAvailability.getStartTime()));
            statement.setTime(4, java.sql.Time.valueOf(studentAvailability.getEndTime()));
            statement.setString(5, studentAvailability.getAvailabilityId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteStudentAvailability(StudentAvailability mentorAvailability) {
        String query = "DELETE FROM student_availability WHERE availability_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentorAvailability.getAvailabilityId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<StudentAvailability> getAvailabilityByStudentId(String studentId) {
        String query = "SELECT * FROM student_availability WHERE student_id=?";
        List<StudentAvailability> availabilities = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                availabilities.add(new StudentAvailability(
                    rs.getString("availability_id"),
                    rs.getString("student_id"),
                    StudentAvailability.DayOfWeeks.valueOf(rs.getString("day_of_week")),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availabilities;
    }
}
