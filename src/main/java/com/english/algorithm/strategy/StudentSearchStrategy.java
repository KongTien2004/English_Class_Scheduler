package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.Student;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentSearchStrategy implements SearchableEntity<Student> {
    @Override
    public double scoreCalculation(Student student, Map<String, Object> criteria) {
        double score = 0.0;

        String ieltsType = (String) criteria.get("ielts_type");
        if (ieltsType != null && student.getIeltsType().name().equals(ieltsType)) score += 30.0;

        Double minBand = (Double) criteria.get("minTargetBand");
        Double maxBand = (Double) criteria.get("maxTargetBand");
        if (minBand != null && maxBand != null) {
            if (student.getTargetBand() >= minBand && student.getTargetBand() <= maxBand) score += 30.0;
        }

        Double minCurrentBand = (Double) criteria.get("minCurrentBand");
        if (minCurrentBand != null) {
            double currentOverall = (
                    student.getCurrentListeningBand() +
                    student.getCurrentReadingBand() +
                    student.getCurrentWritingBand() +
                    student.getCurrentSpeakingBand()
            ) / 4.0;
            if (currentOverall >= minCurrentBand) score += 20.0;
        }

        String centerId = (String) criteria.get("preferredCenterId");
        if (centerId != null && centerId.equals(student.getPreferredCenterId())) score += 20.0;

        return score;
    }

    @Override
    public List<Student> filterByCriteria(List<Student> students, Map<String, Object> criteria) {
        String ieltsType = (String) criteria.get("ieltsType");
        String centerId = (String) criteria.get("preferredCenterId");

        return students.stream()
                .filter(s -> ieltsType == null || s.getIeltsType().name().equalsIgnoreCase(ieltsType))
                .filter(s -> centerId == null || centerId.equals(s.getPreferredCenterId()))
                .collect(Collectors.toList());
    }
}
