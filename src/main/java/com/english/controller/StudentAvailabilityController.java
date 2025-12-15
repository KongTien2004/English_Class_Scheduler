package com.english.controller;

import com.english.model.StudentAvailability;
import com.english.service.StudentAvailabilityService;

import javax.swing.*;

public class StudentAvailabilityController {
    private StudentAvailabilityService studentAvailabilityService;

    public StudentAvailabilityController(StudentAvailabilityService studentAvailabilityService) {
        this.studentAvailabilityService = studentAvailabilityService;
    }

    public void insertStudentAvailability(StudentAvailability studentAvailability, JFrame frame) {
        if (studentAvailabilityService.insertStudentAvailability(studentAvailability)) {
            JOptionPane.showMessageDialog(frame,
                    "Student's leisure time has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateStudentAvailability(StudentAvailability studentAvailability, JFrame frame) {
        if (studentAvailabilityService.updateStudentAvailability(studentAvailability)) {
            JOptionPane.showMessageDialog(frame,
                    "Student's leisure time has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteStudentAvailability(StudentAvailability studentAvailability, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are your sure to delete it?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (studentAvailabilityService.deleteStudentAvailability(studentAvailability)) {
                JOptionPane.showMessageDialog(frame,
                        "Student's leisure time has been deleted!",
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
}
