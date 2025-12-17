package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.LearningPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LearningPlanSearchStrategy implements SearchableEntity<LearningPlan> {

    /**
     * Calculates a score for a learning plan based on a given set of criteria.
     * The score indicates how well the plan matches the criteria.
     *
     * @param entity   The learning plan to be scored.
     * @param criteria A map of criteria to score against. Keys can be "targetBand", "remainingSessions", "startDate".
     * @return A double representing the score of the learning plan.
     */
    @Override
    public double scoreCalculation(LearningPlan entity, Map<String, Object> criteria) {
        double score = 1.0;

        // Score based on target band
        if (criteria.containsKey("targetBand") && criteria.get("targetBand") instanceof Number) {
            double desiredBand = ((Number) criteria.get("targetBand")).doubleValue();
            double entityBand = entity.getTargetBand();
            if (entityBand >= desiredBand) {
                score += (entityBand - desiredBand) * 0.1; // Bonus for exceeding target
            } else {
                score -= (desiredBand - entityBand) * 0.2; // Penalty for not meeting target
            }
        }

        // Score based on remaining sessions
        if (criteria.containsKey("remainingSessions") && criteria.get("remainingSessions") instanceof Number) {
            int desiredSessions = ((Number) criteria.get("remainingSessions")).intValue();
            int entitySessions = entity.getRemainingSessions();
            if (entitySessions >= desiredSessions) {
                score += 0.5; // Bonus for having enough sessions
            }
        }

        // Score based on start date proximity
        if (criteria.containsKey("startDate") && criteria.get("startDate") instanceof LocalDate) {
            LocalDate desiredDate = (LocalDate) criteria.get("startDate");
            LocalDate entityDate = entity.getStartDate();
            if (entityDate != null) {
                long dayDifference = java.time.temporal.ChronoUnit.DAYS.between(desiredDate, entityDate);
                // Higher score for closer start date
                score += 1.0 / (Math.abs(dayDifference) + 1);
            }
        }

        return Math.max(0, score); // Ensure score is non-negative
    }

    /**
     * Filters a list of learning plans based on a given set of criteria.
     *
     * @param entities The list of learning plans to filter.
     * @param criteria A map of criteria to filter by. Keys can include "studentId", "mentorId",
     *                 "ieltsType", "planStatus", "minTargetBand", "maxTargetBand",
     *                 "minRemainingSessions", "startDateAfter", "startDateBefore".
     * @return A list of learning plans that match the criteria.
     */
    @Override
    public List<LearningPlan> filterByCriteria(List<LearningPlan> entities, Map<String, Object> criteria) {
        Stream<LearningPlan> stream = entities.stream();

        // Filter by studentId
        if (criteria.containsKey("studentId") && criteria.get("studentId") instanceof String) {
            String studentId = (String) criteria.get("studentId");
            stream = stream.filter(p -> p.getStudentId() != null && p.getStudentId().equals(studentId));
        }

        // Filter by mentorId
        if (criteria.containsKey("mentorId") && criteria.get("mentorId") instanceof String) {
            String mentorId = (String) criteria.get("mentorId");
            stream = stream.filter(p -> p.getMentorId() != null && p.getMentorId().equals(mentorId));
        }

        // Filter by IELTS type
        if (criteria.containsKey("ieltsType") && criteria.get("ieltsType") instanceof LearningPlan.IELTSType) {
            LearningPlan.IELTSType ieltsType = (LearningPlan.IELTSType) criteria.get("ieltsType");
            stream = stream.filter(p -> p.getIeltsType() == ieltsType);
        }

        // Filter by plan status
        if (criteria.containsKey("planStatus") && criteria.get("planStatus") instanceof LearningPlan.PlanStatus) {
            LearningPlan.PlanStatus planStatus = (LearningPlan.PlanStatus) criteria.get("planStatus");
            stream = stream.filter(p -> p.getPlanStatus() == planStatus);
        } else {
            // Default to filtering for ACTIVE plans if no status is specified
            stream = stream.filter(p -> p.getPlanStatus() == LearningPlan.PlanStatus.ACTIVE);
        }

        // Filter by target band range
        if (criteria.containsKey("minTargetBand") && criteria.get("minTargetBand") instanceof Number) {
            double minBand = ((Number) criteria.get("minTargetBand")).doubleValue();
            stream = stream.filter(p -> p.getTargetBand() >= minBand);
        }
        if (criteria.containsKey("maxTargetBand") && criteria.get("maxTargetBand") instanceof Number) {
            double maxBand = ((Number) criteria.get("maxTargetBand")).doubleValue();
            stream = stream.filter(p -> p.getTargetBand() <= maxBand);
        }

        // Filter by minimum remaining sessions
        if (criteria.containsKey("minRemainingSessions") && criteria.get("minRemainingSessions") instanceof Number) {
            int minSessions = ((Number) criteria.get("minRemainingSessions")).intValue();
            stream = stream.filter(p -> p.getRemainingSessions() >= minSessions);
        }

        // Filter by start date range
        if (criteria.containsKey("startDateAfter") && criteria.get("startDateAfter") instanceof LocalDate) {
            LocalDate afterDate = (LocalDate) criteria.get("startDateAfter");
            stream = stream.filter(p -> p.getStartDate() != null && p.getStartDate().isAfter(afterDate));
        }
        if (criteria.containsKey("startDateBefore") && criteria.get("startDateBefore") instanceof LocalDate) {
            LocalDate beforeDate = (LocalDate) criteria.get("startDateBefore");
            stream = stream.filter(p -> p.getStartDate() != null && p.getStartDate().isBefore(beforeDate));
        }

        return stream.collect(Collectors.toList());
    }
}