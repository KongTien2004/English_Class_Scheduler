package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.MentorAvailability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;

public class MentorAvailabilityDAO {
    public boolean insertMentorAvailability(MentorAvailability mentorAvailability) {
        String query = "INSERT INTO mentor_availability (availability_id, mentor_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentorAvailability.getAvailabilityId());
            statement.setString(2, mentorAvailability.getMentorId());
            statement.setString(3, mentorAvailability.getDayOfWeek().toString());
            statement.setTime(4, java.sql.Time.valueOf(mentorAvailability.getStartTime()));
            statement.setTime(5, java.sql.Time.valueOf(mentorAvailability.getEndTime()));

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateMentorAvailability(MentorAvailability mentorAvailability) {
        String query = "UPDATE mentor_availability SET mentor_id=?, day_of_week=?, start_time=?, end_time=? WHERE availability_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentorAvailability.getMentorId());
            statement.setString(2, mentorAvailability.getDayOfWeek().toString());
            statement.setTime(4, java.sql.Time.valueOf(mentorAvailability.getStartTime()));
            statement.setTime(5, java.sql.Time.valueOf(mentorAvailability.getEndTime()));
            statement.setString(6, mentorAvailability.getAvailabilityId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteMentorAvailability(MentorAvailability mentorAvailability) {
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
