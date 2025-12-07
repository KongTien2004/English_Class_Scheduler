package com.english.DAO;

import com.english.model.Mentor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MentorDAO {
    private Connection connection;

    public MentorDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertMentor(Mentor mentor) {
        String query = "INSERT INTO mentor (mentor_id, mentor_name, email, certified_band, can_teach_general, can_teach_academic, is_available VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentor.getMentorId());
            statement.setString(2, mentor.getMentorName());
            statement.setString(3, mentor.getEmail());
            statement.setDouble(4, mentor.getCertifiedBand());
            statement.setBoolean(5, mentor.isCanTeachGeneral());
            statement.setBoolean(6, mentor.isCanTeachAcademic());
            statement.setBoolean(7, mentor.isAvailable());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMentor(Mentor mentor) {
        String query = "UPDATE mentor SET mentor_name=?, email=?, certified_band=?, can_teach_general=?, can_teach_academic=?, is_available=? WHERE mentor_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentor.getMentorName());
            statement.setString(2, mentor.getEmail());
            statement.setDouble(3, mentor.getCertifiedBand());
            statement.setBoolean(4, mentor.isCanTeachGeneral());
            statement.setBoolean(5, mentor.isCanTeachAcademic());
            statement.setBoolean(6, mentor.isAvailable());
            statement.setString(7, mentor.getMentorId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMentor(Mentor mentor) {
        String query = "DELETE FROM mentor WHERE mentor_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentor.getMentorId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Mentor> getAllMentors() {
        String query = "SELECT * FROM mentor";
        List<Mentor> mentors = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                mentors.add(mapResultSetToMentor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mentors;
    }

    private Mentor mapResultSetToMentor(ResultSet rs) throws SQLException {
        Mentor mentor = new Mentor();
        mentor.setMentorId(rs.getString("mentor_id"));
        mentor.setMentorName(rs.getString("mentor_name"));
        mentor.setEmail(rs.getString("email"));
        mentor.setCertifiedBand(rs.getDouble("certified_band"));
        mentor.setCanTeachGeneral(rs.getBoolean("can_teach_general"));
        mentor.setCanTeachAcademic(rs.getBoolean("can_teach_academic"));
        mentor.setAvailable(rs.getBoolean("is_available"));

        return mentor;
    }

    public Mentor getMentorById(String mentorId) {
        String query = "SELECT * FROM mentor WHERE mentor_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mentorId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapResultSetToMentor(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
