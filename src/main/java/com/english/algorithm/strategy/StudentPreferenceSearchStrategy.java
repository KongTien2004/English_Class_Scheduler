package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.StudentPreference;
import com.english.model.StudentPreference.DayOfWeeks;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentPreferenceSearchStrategy implements SearchableEntity<StudentPreference> {
    @Override
    public double scoreCalculation(StudentPreference studentPreference, Map<String, Object> criteria) {
        double score = 0.0;

        if (criteria.containsKey("preferredCenter") && studentPreference.getPreferredCenter() != null) {
            String preferredCenter = (String) criteria.get("preferredCenter");
            if (studentPreference.getPreferredCenter().toLowerCase().contains(preferredCenter.toLowerCase())) {
                score += 50;
            }
        }

        if (criteria.containsKey("dayOfWeek") && studentPreference.getDayOfWeek() == criteria.get("dayOfWeek")) {
            score += 50; // Bonus for the correct day
        }

        if (criteria.containsKey("preferredStart") && criteria.containsKey("preferredEnd")) {
            LocalTime requiredStartTime = (LocalTime) criteria.get("preferredStart");
            LocalTime requiredEndTime = (LocalTime) criteria.get("preferredEnd");

            LocalTime availableStartTime = studentPreference.getPreferredStart();
            LocalTime availableEndTime = studentPreference.getPreferredEnd();

            // Check for overlap
            if (availableStartTime.isBefore(requiredEndTime) && availableEndTime.isAfter(requiredStartTime)) {
                // Calculate overlapping time
                LocalTime overlapStart = availableStartTime.isAfter(requiredStartTime) ? availableStartTime : requiredStartTime;
                LocalTime overlapEnd = availableEndTime.isBefore(requiredEndTime) ? availableEndTime : requiredEndTime;
                long overlapMinutes = java.time.Duration.between(overlapStart, overlapEnd).toMinutes();
                score += overlapMinutes; // Add 1 point per minute of overlap
            }
        }

        return score;
    }

    @Override
    public List<StudentPreference> filterByCriteria(List<StudentPreference> studentPreferences, Map<String, Object> criteria) {
        return studentPreferences.stream()
                .filter(preference -> {
                    if (criteria.containsKey("studentId") && !preference.getStudentId().equals(criteria.get("studentId"))) {
                        return false;
                    }
                    if (criteria.containsKey("preferredCenter") && preference.getPreferredCenter() != null) {
                        String preferredCenter = (String) criteria.get("preferredCenter");
                        if (!preference.getPreferredCenter().toLowerCase().contains(preferredCenter.toLowerCase())) {
                            return false;
                        }
                    }
                    if (criteria.containsKey("dayOfWeek") && preference.getDayOfWeek() != criteria.get("dayOfWeek")) {
                        return false;
                    }
                    if (criteria.containsKey("preferredStart") && criteria.containsKey("preferredEnd")) {
                        LocalTime requiredStartTime = (LocalTime) criteria.get("preferredStart");
                        LocalTime requiredEndTime = (LocalTime) criteria.get("preferredEnd");
                        LocalTime availableStartTime = preference.getPreferredStart();
                        LocalTime availableEndTime = preference.getPreferredEnd();
                        // No overlap if available slot is completely before or after required slot
                        if (availableEndTime.isBefore(requiredStartTime) || availableStartTime.isAfter(requiredEndTime)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
