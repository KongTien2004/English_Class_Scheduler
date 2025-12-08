package com.english.controller;

import com.english.model.LearningSession;
import com.english.service.LearningSessionService;

import javax.swing.*;
import java.util.List;

public class LearningSessionController {
    private LearningSessionService learningSessionService;

    public LearningSessionController(LearningSessionService learningSessionService) {
        this.learningSessionService = learningSessionService;
    }

    public void insertLearningSession(LearningSession learningSession, JFrame frame) {
        if (learningSessionService.insertLearningSession(learningSession)) {
            JOptionPane.showMessageDialog(frame,
                    "Session has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateLearningSession(LearningSession learningSession, JFrame frame) {
        if (learningSessionService.updateLearningSession(learningSession)) {
            JOptionPane.showMessageDialog(frame,
                    "Session has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating session failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteLearningSession(LearningSession learningSession, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this session?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (learningSessionService.deleteLearningSession(learningSession)) {
                JOptionPane.showMessageDialog(frame,
                        "Session has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting session failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public LearningSession getLearningSessionById(String sessionId) {
        return learningSessionService.getLearningSessionById(sessionId);
    }

    public List<LearningSession> getAllLearningSessions() {
        return learningSessionService.getAllLearningSessions();
    }

    public int totalLearningSessions() {
        return learningSessionService.totalLearningSessions();
    }
}
