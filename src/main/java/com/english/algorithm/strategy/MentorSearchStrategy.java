package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.Mentor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MentorSearchStrategy implements SearchableEntity<Mentor> {
    @Override
    public double scoreCalculation(Mentor mentor, Map<String, Object> criteria) {
        double score = 0.0;

        if (!mentor.isAvailable()) return 0.0;

        String ieltsType = (String) criteria.get("ieltsType");
        if (ieltsType != null) {
            if (ieltsType.equals("GENERAL") && mentor.isCanTeachGeneral()) {
                score += 40.0;
            } else if (ieltsType.equals("ACADEMIC") && mentor.isCanTeachAcademic()) {
                score += 40.0;
            }
        }

        Double targetBand = (Double) criteria.get("targetBand");
        if (targetBand != null) {
            score += bandMatchScoreCalculation(mentor.getCertifiedBand(), targetBand);
        }

        score += completenessScoreCalculation(mentor);

        return score;
    }

    private double bandMatchScoreCalculation(double certifiedBand, Double targetBand) {
        if (certifiedBand < targetBand) return 0.0;
        double difference = certifiedBand - targetBand;
        if (difference <= 1.0) return 30.0;
        if (difference <= 2.0) return 20.0;
        return 10.0;
    }

    private double completenessScoreCalculation(Mentor mentor) {
        double score = 0.0;

        if (mentor.getMentorId() != null && !mentor.getMentorId().trim().isEmpty()) score += 5.0;
        if (mentor.getMentorName() != null && !mentor.getMentorName().trim().isEmpty()) score += 5.0;
        if (mentor.getEmail() != null && !mentor.getEmail().trim().isEmpty()) score += 5.0;
        if (mentor.getCertifiedBand() > 0) score += 5.0;

        return score;
    }

    @Override
    public List<Mentor> filterByCriteria(List<Mentor> mentors, Map<String, Object> criteria) {
        String ieltsType = (String) criteria.get("ieltsType");
        Double targetBand = (Double) criteria.get("targetBand");

        return mentors.stream()
                .filter(mentor -> mentor.isAvailable())
                .filter(mentor -> {
                    if (ieltsType == null) return true;
                    if (ieltsType.equals("GENERAL")) return mentor.isCanTeachGeneral();
                    if (ieltsType.equals("ACADEMIC")) return mentor.isCanTeachAcademic();
                    return true;
                })
                .filter(mentor -> targetBand == null || mentor.getCertifiedBand() >= targetBand)
                .collect(Collectors.toList());
    }
}
