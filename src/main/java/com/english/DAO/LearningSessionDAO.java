package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.LearningSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LearningSessionDAO {
    public boolean insertLearningSession(LearningSession learningSession) {
        String query = "INSERT INTO learning_session (session_id, plan_id, session_number, session_type, scheduled_time, actual_start, actual_end, location, session_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, learningSession.getSessionId());
            statement.setString(2, learningSession.getPlanId());
            statement.setInt(3, learningSession.getSessionNumber());
            statement.setString(4, learningSession.getSessionType().name());
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(learningSession.getScheduledTime()));
            statement.setTimestamp(6, java.sql.Timestamp.valueOf(learningSession.getActualStart()));
            statement.setTimestamp(7, java.sql.Timestamp.valueOf(learningSession.getActualEnd()));
            statement.setString(8, learningSession.getLocation());
            statement.setString(9, learningSession.getSessionStatus().name());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLearningSession(LearningSession learningSession) {
        String query = "UPDATE learning_session SET plan_id=?, session_number=?, session_type=?, scheduled_time=?, actual_start=?, actual_end=?, location=?, session_status=? WHERE session_id = ?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, learningSession.getPlanId());
            statement.setInt(2, learningSession.getSessionNumber());
            statement.setString(3, learningSession.getSessionType().name());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(learningSession.getScheduledTime()));
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(learningSession.getActualStart()));
            statement.setTimestamp(6, java.sql.Timestamp.valueOf(learningSession.getActualEnd()));
            statement.setString(7, learningSession.getLocation());
            statement.setString(8, learningSession.getSessionStatus().name());
            statement.setString(9, learningSession.getSessionId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLearningSession(LearningSession learningSession) {
        String query = "DELETE FROM learning_session WHERE session_id = ?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, learningSession.getSessionId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LearningSession getLearningSessionById(String sessionId) {
        String query = "SELECT * FROM learning_session WHERE session_id = ?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, sessionId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapResultSetToLearningSession(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<LearningSession> getAllLearningSessions() {
        String query = "SELECT * FROM learning_session";
        List<LearningSession> learningSessions = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                learningSessions.add(mapResultSetToLearningSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return learningSessions;
    }

    private LearningSession mapResultSetToLearningSession(ResultSet rs) throws SQLException {
        LearningSession learningSession = new LearningSession();

        learningSession.setSessionId(rs.getString("session_id"));
        learningSession.setPlanId(rs.getString("plan_id"));
        learningSession.setSessionNumber(rs.getInt("session_number"));
        learningSession.setSessionType(LearningSession.SessionType.valueOf(rs.getString("session_type")));
        learningSession.setScheduledTime(rs.getTimestamp("scheduled_time").toLocalDateTime());
        learningSession.setActualStart(rs.getTimestamp("actual_start").toLocalDateTime());
        learningSession.setActualEnd(rs.getTimestamp("actual_end").toLocalDateTime());
        learningSession.setLocation(rs.getString("location"));
        learningSession.setSessionStatus(LearningSession.SessionStatus.valueOf(rs.getString("session_status")));

        return learningSession;
    }

    public List<LearningSession> getSessionsByPlan (String planId) {
        String query = "SELECT * FROM learning_session WHERE plan_id = ?";
        List<LearningSession> learningSessions = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, planId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                learningSessions.add(mapResultSetToLearningSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return learningSessions;
    }
}
