package com.english.controller;

import com.english.model.LearningPlan;
import com.english.service.LearningPlanService;

import javax.swing.*;
import java.util.List;

public class LearningPlanController {
    private LearningPlanService learningPlanService;

    public LearningPlanController(LearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    public void insertLearningPlan(LearningPlan learningPlan, JFrame frame) {
        if (learningPlanService.insertLearningPlan(learningPlan)) {
            JOptionPane.showMessageDialog(frame,
                    "Plan has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "PLease check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateLearningPlan(LearningPlan learningPlan, JFrame frame) {
        if (learningPlanService.updateLearningPlan(learningPlan)) {
            JOptionPane.showMessageDialog(frame,
                    "Plan has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating plan failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteLearningPlan(LearningPlan learningPlan, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this plan?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (learningPlanService.deleteLearningPlan(learningPlan)) {
                JOptionPane.showMessageDialog(frame,
                        "Plan has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting plan failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public LearningPlan getLearningPlanById(String planId) {
        return learningPlanService.getLearningPlanById(planId);
    }

    public List<LearningPlan> getAllLearningPlans() {
        return learningPlanService.getAllLearningPlans();
    }

    public int totalLearningPlans() {
        return learningPlanService.totalLearningPlans();
    }
}
