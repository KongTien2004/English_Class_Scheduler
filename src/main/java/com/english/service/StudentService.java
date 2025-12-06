package com.english.service;

import com.english.DAO.StudentDAO;
import com.english.model.Student;

import java.util.List;

public class StudentService {
    private StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public boolean insertStudent(Student student) {
        if (!validateStudent(student)) return false;
        return studentDAO.insertStudent(student);
    }

    public boolean updateStudent(Student student) {
        if (!validateStudent(student)) return false;
        return studentDAO.updateStudent(student);
    }

    public boolean deleteStudent(Student student) {
        if (!validateStudent(student)) return false;
        return studentDAO.deleteStudent(student);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public Student getStudentById(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) return null;
        return studentDAO.getStudentById(studentId);
    }

    private boolean validateStudent(Student student) {
        if (student == null) return false;
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) return false;
        if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) return false;
        if (student.getIeltsType() == null) return false;
        if (student.getTargetBand() <= 0) return false;
        return true;
    }

    public int totalStudents() {
        return studentDAO.getAllStudents().size();
    }
}
