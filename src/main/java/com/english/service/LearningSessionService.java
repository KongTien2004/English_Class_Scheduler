package com.english.service;

import com.english.DAO.LearningSessionDAO;
import com.english.model.LearningSession;

import java.util.List;

public class LearningSessionService {
    private LearningSessionDAO learningSessionDAO;

    public LearningSessionService(LearningSessionDAO learningSessionDAO) {
        this.learningSessionDAO = learningSessionDAO;
    }

    public boolean insertLearningSession(LearningSession learningSession) {
        if (!validateSession(learningSession)) return false;
        return learningSessionDAO.insertLearningSession(learningSession);
    }

    public boolean updateLearningSession(LearningSession learningSession) {
        if (!validateSession(learningSession)) return false;
        return learningSessionDAO.updateLearningSession(learningSession);
    }

    public boolean deleteLearningSession(LearningSession learningSession) {
        if (!validateSession(learningSession)) return false;
        return learningSessionDAO.deleteLearningSession(learningSession);
    }

    public LearningSession getLearningSessionById(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) return null;
        return learningSessionDAO.getLearningSessionById(sessionId);
    }

    public List<LearningSession> getAllLearningSessions() {
        return learningSessionDAO.getAllLearningSessions();
    }

    public List<LearningSession> getSessionByPlan (String planId) {
        return learningSessionDAO.getSessionsByPlan(planId);
    }

    private boolean validateSession(LearningSession learningSession) {
        if (learningSession == null) return false;
        if (learningSession.getSessionId() == null) return false;
        if (learningSession.getPlanId() == null) return false;
        if (learningSession.getSessionNumber() <= 0) return false;
        return true;
    }

    public int totalLearningSessions() {
        return learningSessionDAO.getAllLearningSessions().size();
    }
}
