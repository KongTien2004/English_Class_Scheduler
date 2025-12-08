package com.english.DAO;

import com.english.model.ProgressRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgressRecordDAO {
    private Connection connection;

    public ProgressRecordDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertProgressRecord(ProgressRecord progressRecord) {
        String query = "INSERT INTO progress_record (progress_id, student_id, recorded_date, listening_band, reading_band, writing_band, speaking_band, overall_band) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, progressRecord.getProgressId());
            statement.setString(2, progressRecord.getStudentId());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(progressRecord.getRecordedDate()));
            statement.setDouble(4, progressRecord.getListeningBand());
            statement.setDouble(5, progressRecord.getReadingBand());
            statement.setDouble(6, progressRecord.getWritingBand());
            statement.setDouble(7, progressRecord.getSpeakingBand());
            statement.setDouble(8, progressRecord.getOverallBand());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProgressRecord(ProgressRecord progressRecord) {
        String query = "UPDATE progress_record SET student_id=?, recorded_date=?, listening_band=?, reading_band=?, writing_band=?, speaking_band=?, overall_band=? WHERE progress_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, progressRecord.getStudentId());
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(progressRecord.getRecordedDate()));
            statement.setDouble(3, progressRecord.getListeningBand());
            statement.setDouble(4, progressRecord.getReadingBand());
            statement.setDouble(5, progressRecord.getWritingBand());
            statement.setDouble(6, progressRecord.getSpeakingBand());
            statement.setDouble(7, progressRecord.getOverallBand());
            statement.setString(8, progressRecord.getProgressId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProgressRecord(ProgressRecord progressRecord) {
        String query = "DELETE FROM progress_record WHERE progress_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, progressRecord.getProgressId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ProgressRecord getProgressRecordById(String progressId) {
        String query = "SELECT * FROM progress_record WHERE progress_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, progressId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapResultSetToProgressRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ProgressRecord> getAllProgressRecords() {
        String query = "SELECT * FROM progress_record";
        List<ProgressRecord> records = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                records.add(mapResultSetToProgressRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    private ProgressRecord mapResultSetToProgressRecord(ResultSet rs) throws SQLException {
        ProgressRecord progressRecord = new ProgressRecord();

        progressRecord.setProgress_id(rs.getString("progress_id"));
        progressRecord.setStudent_id(rs.getString("student_id"));
        progressRecord.setRecordedDate(rs.getTimestamp("recorded_date").toLocalDateTime());
        progressRecord.setListeningBand(rs.getDouble("listening_band"));
        progressRecord.setReadingBand(rs.getDouble("reading_band"));
        progressRecord.setWritingBand(rs.getDouble("writing_band"));
        progressRecord.setSpeakingBand(rs.getDouble("speaking_band"));
        progressRecord.setOverallBand(rs.getDouble("overall_band"));

        return progressRecord;
    }
}
