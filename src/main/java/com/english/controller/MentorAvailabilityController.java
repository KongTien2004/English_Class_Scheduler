package com.english.controller;

import com.english.model.MentorAvailability;
import com.english.service.MentorAvailabilityService;

import javax.swing.*;

public class MentorAvailabilityController {
    private MentorAvailabilityService mentorAvailabilityService;

    public MentorAvailabilityController(MentorAvailabilityService mentorAvailabilityService) {
        this.mentorAvailabilityService = mentorAvailabilityService;
    }

    public void insertMentorAvailability(MentorAvailability mentorAvailability, JFrame frame) {
        if (mentorAvailabilityService.insertMentorAvailability(mentorAvailability)) {
            JOptionPane.showMessageDialog(frame,
                    "Mentor's leisure time has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateMentorAvailability(MentorAvailability mentorAvailability, JFrame frame) {
        if (mentorAvailabilityService.updateMentorAvailability(mentorAvailability)) {
            JOptionPane.showMessageDialog(frame,
                    "Mentor's leisure time has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteMentorAvailability(MentorAvailability mentorAvailability, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are your sure to delete it?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (mentorAvailabilityService.deleteMentorAvailability(mentorAvailability)) {
                JOptionPane.showMessageDialog(frame,
                        "Mentor's leisure time has been deleted!",
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
