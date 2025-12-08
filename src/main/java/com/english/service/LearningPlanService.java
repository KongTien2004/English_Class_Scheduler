package com.english.service;

import com.english.DAO.LearningPlanDAO;
import com.english.model.LearningPlan;

import java.util.List;

public class LearningPlanService {
    private LearningPlanDAO learningPlanDAO;

    public LearningPlanService(LearningPlanDAO learningPlanDAO) {
        this.learningPlanDAO = learningPlanDAO;
    }

    public boolean insertLearningPlan(LearningPlan learningPlan) {
        if (!validatePlan(learningPlan)) return false;
        return learningPlanDAO.insertLearningPlan(learningPlan);
    }

    public boolean updateLearningPlan(LearningPlan learningPlan) {
        if (!validatePlan(learningPlan)) return false;
        return learningPlanDAO.updateLearningPlan(learningPlan);
    }

    public boolean deleteLearningPlan(LearningPlan learningPlan) {
        if (!validatePlan(learningPlan)) return false;
        return learningPlanDAO.deleteLearningPlan(learningPlan);
    }

    public LearningPlan getLearningPlanById(String planId) {
        if (planId == null || planId.trim().isEmpty()) return null;
        return learningPlanDAO.getLearningPLanById(planId);
    }

    public List<LearningPlan> getAllLearningPlans() {
        return learningPlanDAO.getAllLearningPlans();
    }

    private boolean validatePlan(LearningPlan learningPlan) {
        if (learningPlan == null) return false;
        if (learningPlan.getPlanId() == null || learningPlan.getPlanId().trim().isEmpty()) return false;
        if (learningPlan.getStudentId() == null || learningPlan.getStudentId().trim().isEmpty()) return false;
        if (learningPlan.getMentorId() == null || learningPlan.getMentorId().trim().isEmpty()) return false;
        if (learningPlan.getIeltsType() == null) return false;
        if (learningPlan.getTargetBand() <= 0) return false;
        if (learningPlan.getTotalSessions() <= 0) return false;
        if (learningPlan.getRemainingSessions() <= 0) return false;

        return true;
    }

    public int totalLearningPlans() {
        return learningPlanDAO.getAllLearningPlans().size();
    }
}
