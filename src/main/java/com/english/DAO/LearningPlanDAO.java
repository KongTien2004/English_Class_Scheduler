package com.english.DAO;

import com.english.model.LearningPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LearningPlanDAO {
    private Connection connection;

    public LearningPlanDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertLearningPlan(LearningPlan learningPlan) {
        String query = "INSERT INTO learning_plan (plan_id, student_id, mentor_id, ielts_type, target_band, total_sessions, remaining_sessions, start_date, plan_status, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, learningPlan.getPlanId());
            statement.setString(2, learningPlan.getStudentId());
            statement.setString(3, learningPlan.getMentorId());
            statement.setString(4, learningPlan.getIeltsType().name());
            statement.setDouble(5, learningPlan.getTargetBand());
            statement.setInt(6, learningPlan.getTotalSessions());
            statement.setInt(7, learningPlan.getRemainingSessions());
            statement.setDate(8, java.sql.Date.valueOf(learningPlan.getStartDate()));
            statement.setString(9, learningPlan.getPlanStatus().name());
            statement.setTimestamp(10, java.sql.Timestamp.valueOf(learningPlan.getCreatedDate()));

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLearningPlan(LearningPlan learningPlan) {
        String query = "UPDATE learning_plan SET student_id=?, mentor_id=?, ielts_type=?, target_band=?, total_sessions=?, remaining_sessions=?, start_date=?, plan_status=?, created_date=? WHERE plan_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, learningPlan.getStudentId());
            statement.setString(2, learningPlan.getMentorId());
            statement.setString(3, learningPlan.getIeltsType().name());
            statement.setDouble(4, learningPlan.getTargetBand());
            statement.setInt(5, learningPlan.getTotalSessions());
            statement.setInt(6, learningPlan.getRemainingSessions());
            statement.setDate(7, java.sql.Date.valueOf(learningPlan.getStartDate()));
            statement.setString(8, learningPlan.getPlanStatus().name());
            statement.setTimestamp(9, java.sql.Timestamp.valueOf(learningPlan.getCreatedDate()));
            statement.setString(10, learningPlan.getPlanId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLearningPlan(LearningPlan learningPlan) {
        String query = "DELETE FROM learning_plan WHERE plan_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, learningPlan.getPlanId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LearningPlan getLearningPLanById(String planId) {
        String query = "SELECT * FROM learning_plan WHERE plan_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, planId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapResultSetToLearningPlan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<LearningPlan> getAllLearningPlans() {
        List<LearningPlan> learningPlans = new ArrayList<>();
        String query = "SELECT * FROM learning_plan";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                learningPlans.add(mapResultSetToLearningPlan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return learningPlans;
    }

    private LearningPlan mapResultSetToLearningPlan(ResultSet rs) throws SQLException {
        LearningPlan learningPlan = new LearningPlan();

        learningPlan.setPlanId(rs.getString("plan_id"));
        learningPlan.setStudentId(rs.getString("student_id"));
        learningPlan.setMentorId(rs.getString("mentor_id"));
        learningPlan.setIeltsType(LearningPlan.IELTSType.valueOf(rs.getString("ielts_type")));
        learningPlan.setTargetBand(rs.getDouble("target_band"));
        learningPlan.setTotalSessions(rs.getInt("total_sessions"));
        learningPlan.setRemainingSessions(rs.getInt("remaining_sessions"));
        learningPlan.setStartDate(rs.getDate("start_date").toLocalDate());
        learningPlan.setPlanStatus(LearningPlan.PlanStatus.valueOf(rs.getString("plan_status")));
        learningPlan.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());

        return learningPlan;
    }
}
