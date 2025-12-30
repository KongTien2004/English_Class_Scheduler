package com.english.controller;

import com.english.model.Mentor;
import com.english.service.MentorService;

import javax.swing.*;
import java.util.List;

public class MentorController {
    private MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    public void insertMentor(Mentor mentor, JFrame frame) {
        if (mentorService.insertMentor(mentor)) {
            JOptionPane.showMessageDialog(frame,
                    "Mentor has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateMentor(Mentor mentor, JFrame frame) {
        if (mentorService.updateMentor(mentor)) {
            JOptionPane.showMessageDialog(frame,
                    "Mentor has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating mentor failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteMentor(Mentor mentor, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are your sure to delete this mentor?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (mentorService.deleteMentor(mentor)) {
                JOptionPane.showMessageDialog(frame,
                        "Mentor has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting mentor failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Mentor> getAllMentors() {
        return mentorService.getAllMentors();
    }

    public Mentor getMentorById(String mentorId) {
        return mentorService.getMentorById(mentorId);
    }

    public List<Mentor> getAvailableMentors() {
        return mentorService.getAvailableMentors();
    }

    public List<Mentor> getMentorTeachGeneral() {
        return mentorService.getMentorTeachGeneral();
    }

    public List<Mentor> getMentorTeachAcademic() {
        return mentorService.getMentorTeachAcademic();
    }

    public List<Mentor> getMentorsByAddress(String mentorAddress) {
        return mentorService.getMentorsByAddress(mentorAddress);
    }

    public int totalMentors() {
        return mentorService.totalMentors();
    }
}
