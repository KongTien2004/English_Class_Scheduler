package com.english.controller;

import com.english.model.StudentPreference;
import com.english.service.StudentPreferenceService;

import javax.swing.*;
import java.util.List;

public class StudentPreferenceController {
    private StudentPreferenceService studentPreferenceService;

    public StudentPreferenceController(StudentPreferenceService studentPreferenceService) {
        this.studentPreferenceService = studentPreferenceService;
    }

    public void insertPreference(StudentPreference studentPreference, JFrame frame) {
        if (studentPreferenceService.insertPreference(studentPreference)) {
            JOptionPane.showMessageDialog(frame,
                    "Student's preference has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updatePreference(StudentPreference studentPreference, JFrame frame) {
        if (studentPreferenceService.updatePreference(studentPreference)) {
            JOptionPane.showMessageDialog(frame,
                    "Student's preference has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePreference(StudentPreference studentPreference, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete it?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (studentPreferenceService.deletePreference(studentPreference)) {
                JOptionPane.showMessageDialog(frame,
                        "Student's preference has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Please check the information",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public StudentPreference getPreferenceById(String preferenceId) {
        return studentPreferenceService.getPreferenceById(preferenceId);
    }

    public List<StudentPreference> getAllPreferences() {
        return studentPreferenceService.getAllPreferences();
    }

    public List<StudentPreference> getPreferenceByStudent(String studentId) {
        return studentPreferenceService.getPreferenceByStudent(studentId);
    }
}
