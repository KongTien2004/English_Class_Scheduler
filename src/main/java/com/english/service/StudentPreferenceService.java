package com.english.service;

import com.english.DAO.StudentPreferenceDAO;
import com.english.model.StudentPreference;

import java.util.List;

public class StudentPreferenceService {
    private StudentPreferenceDAO studentPreferenceDAO;

    public StudentPreferenceService(StudentPreferenceDAO studentPreferenceDAO) {
        this.studentPreferenceDAO = studentPreferenceDAO;
    }

    public boolean insertPreference(StudentPreference studentPreference) {
        return studentPreferenceDAO.insertStudentPreference(studentPreference);
    }

    public boolean updatePreference(StudentPreference studentPreference) {
        return studentPreferenceDAO.updateStudentPreference(studentPreference);
    }

    public boolean deletePreference(StudentPreference studentPreference) {
        return studentPreferenceDAO.deleteStudentPreference(studentPreference);
    }

    public StudentPreference getPreferenceById(String preferenceId) {
        return studentPreferenceDAO.getStudentPreferenceById(preferenceId);
    }

    public List<StudentPreference> getAllPreferences() {
        return studentPreferenceDAO.getAllPreferences();
    }

    public List<StudentPreference> getPreferenceByStudent(String studentId) {
        return studentPreferenceDAO.getPreferenceByStudent(studentId);
    }
}
