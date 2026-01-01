package com.english.service;

import com.english.DAO.StudentAvailabilityDAO;
import com.english.model.StudentAvailability;

import java.util.List;

public class StudentAvailabilityService {
    private StudentAvailabilityDAO studentAvailabilityDAO;

    public StudentAvailabilityService(StudentAvailabilityDAO studentAvailabilityDAO) {
        this.studentAvailabilityDAO = studentAvailabilityDAO;
    }

    public boolean insertStudentAvailability(StudentAvailability studentAvailability) {
        return studentAvailabilityDAO.insertStudentAvailability(studentAvailability);
    }

    public boolean updateStudentAvailability(StudentAvailability studentAvailability) {
        return studentAvailabilityDAO.updateStudentAvailability(studentAvailability);
    }

    public boolean deleteStudentAvailability(StudentAvailability studentAvailability) {
        return studentAvailabilityDAO.deleteStudentAvailability(studentAvailability);
    }

    public List<StudentAvailability> getAvailabilityByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return studentAvailabilityDAO.getAvailabilityByStudentId(studentId);
    }
}
