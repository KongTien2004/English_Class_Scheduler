package com.english.service;

import com.english.DAO.StudentAvailabilityDAO;
import com.english.model.StudentAvailability;

public class StudentAvailabilityService {
    private StudentAvailabilityDAO studentAvailabilityDAO;

    public StudentAvailabilityService(StudentAvailabilityDAO studentAvailabilityDAO) {
        this.studentAvailabilityDAO = studentAvailabilityDAO;
    }

    public boolean insertStudentAvailability(StudentAvailability studentAvailability) {
        return studentAvailabilityDAO.insertStudentAvailability(studentAvailability);
    }

    public boolean updateStudentAvailability(StudentAvailability studentAvailability) {
        return studentAvailabilityDAO.updateMentorAvailability(studentAvailability);
    }

    public boolean deleteStudentAvailability(StudentAvailability studentAvailability) {
        return studentAvailabilityDAO.deleteMentorAvailability(studentAvailability);
    }
}
