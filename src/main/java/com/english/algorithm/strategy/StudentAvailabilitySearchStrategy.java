package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.StudentAvailability;
import com.english.model.StudentAvailability.DayOfWeeks;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentAvailabilitySearchStrategy implements SearchableEntity<StudentAvailability> {
    @Override
    public double scoreCalculation(StudentAvailability studentAvailability, Map<String, Object> criteria) {
        double score = 0.0;

        if (criteria.containsKey("dayOfWeek") && studentAvailability.getDayOfWeek() == criteria.get("dayOfWeek")) {
            score += 50; // Bonus for the correct day
        }

        if (criteria.containsKey("startTime") && criteria.containsKey("endTime")) {
            LocalTime requiredStartTime = (LocalTime) criteria.get("startTime");
            LocalTime requiredEndTime = (LocalTime) criteria.get("endTime");

            LocalTime availableStartTime = studentAvailability.getStartTime();
            LocalTime availableEndTime = studentAvailability.getEndTime();

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
    public List<StudentAvailability> filterByCriteria(List<StudentAvailability> studentAvailabilities, Map<String, Object> criteria) {
        return studentAvailabilities.stream()
                .filter(availability -> {
                    if (criteria.containsKey("studentId") && !availability.getStudentId().equals(criteria.get("studentId"))) {
                        return false;
                    }
                    if (criteria.containsKey("dayOfWeek") && availability.getDayOfWeek() != criteria.get("dayOfWeek")) {
                        return false;
                    }
                    if (criteria.containsKey("startTime") && criteria.containsKey("endTime")) {
                        LocalTime requiredStartTime = (LocalTime) criteria.get("startTime");
                        LocalTime requiredEndTime = (LocalTime) criteria.get("endTime");
                        LocalTime availableStartTime = availability.getStartTime();
                        LocalTime availableEndTime = availability.getEndTime();
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
