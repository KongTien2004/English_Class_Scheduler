package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public boolean insertStudent(Student student) {
        String query = "INSERT INTO student (student_id, student_name, phone, email, ielts_type, target_band, current_listening_band, current_reading_band, current_writing_band, current_speaking_band, preferred_center_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, student.getStudentId());
            statement.setString(2, student.getStudentName());
            statement.setString(3, student.getPhone());
            statement.setString(4, student.getEmail());
            statement.setString(5, student.getIeltsType().name());
            statement.setDouble(6, student.getTargetBand());
            statement.setDouble(7, student.getCurrentListeningBand());
            statement.setDouble(8, student.getCurrentReadingBand());
            statement.setDouble(9, student.getCurrentWritingBand());
            statement.setDouble(10, student.getCurrentSpeakingBand());
            statement.setString(11, student.getPreferredCenterId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String query = "UPDATE student SET student_name=?, phone=?, email=?, ielts_type=?, target_band=?, current_listening_band=?, current_reading_band=?, current_writing_band=?, current_speaking_band=?, preferred_center_id=? WHERE student_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, student.getStudentName());
            statement.setString(2, student.getPhone());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getIeltsType().name());
            statement.setDouble(5, student.getTargetBand());
            statement.setDouble(6, student.getCurrentListeningBand());
            statement.setDouble(7, student.getCurrentReadingBand());
            statement.setDouble(8, student.getCurrentWritingBand());
            statement.setDouble(9, student.getCurrentSpeakingBand());
            statement.setString(10, student.getPreferredCenterId());
            statement.setString(11, student.getStudentId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(Student student) {
        String query = "DELETE FROM student WHERE student_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, student.getStudentId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getAllStudents() {
        String query = "SELECT * FROM student";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getString("student_id"));
        student.setStudentName(rs.getString("student_name"));
        student.setPhone(rs.getString("phone"));
        student.setEmail(rs.getString("email"));
        student.setIeltsType(Student.IELTSType.valueOf(rs.getString("ielts_type")));
        student.setTargetBand(rs.getDouble("target_band"));
        student.setCurrentListeningBand(rs.getDouble("current_listening_band"));
        student.setCurrentReadingBand(rs.getDouble("current_reading_band"));
        student.setCurrentWritingBand(rs.getDouble("current_writing_band"));
        student.setCurrentSpeakingBand(rs.getDouble("current_speaking_band"));
        student.setPreferredCenterId(rs.getString("preferred_center_id"));

        return student;
    }

    public Student getStudentById(String studentId) {
        String query = "SELECT * FROM student WHERE student_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Student> getStudentByTargetBand(double targetBand) {
        String query = "SELECT * FROM student WHERE target_band=?";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, targetBand);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> getStudentsByAddress(String studentAddress) {
        String query = "SELECT * FROM student WHERE student_address=?";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> getStudentsByIELTSType(Student.IELTSType studentIELTSType) {
        String query = "SELECT * FROM student WHERE ielts_type=?";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ieltsType.name());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
}
