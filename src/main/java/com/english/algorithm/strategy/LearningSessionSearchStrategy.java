package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.LearningSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LearningSessionSearchStrategy implements SearchableEntity<LearningSession> {
    @Override
    public double scoreCalculation(LearningSession session, Map<String, Object> criteria) {
        double score = 0.0;

        String status = (String) criteria.get("status");
        if (status != null && session.getSessionStatus().name().equals(status)) score += 40.0;

        String type = (String) criteria.get("type");
        if (type != null && session.getSessionType().name().equals(type)) score += 30.0;

        LocalDateTime targetDate = (LocalDateTime) criteria.get("targetDate");
        if (targetDate != null && session.getScheduledTime() != null) {
            long hoursDiff = Math.abs(java.time.Duration.between(session.getScheduledTime(), targetDate).toHours());
            score += Math.max(0, 30.0 - hoursDiff);
        }

        return score;
    }

    @Override
    public List<LearningSession> filterByCriteria(List<LearningSession> sessions, Map<String, Object> criteria) {
        String planId = (String) criteria.get("planId");
        String status = (String) criteria.get("status");
        String type = (String) criteria.get("type");

        return sessions.stream()
                .filter(session -> planId == null || planId.equals(session.getPlanId()))
                .filter(session -> status == null || session.getSessionStatus().name().equals(status))
                .filter(session -> type == null || session.getSessionType().name().equals(type))
                .collect(Collectors.toList());
    }
}
