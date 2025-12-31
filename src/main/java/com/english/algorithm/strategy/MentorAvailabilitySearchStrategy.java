package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.MentorAvailability;
import com.english.model.MentorAvailability.DayOfWeeks;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MentorAvailabilitySearchStrategy implements SearchableEntity<MentorAvailability> {
    @Override
    public double scoreCalculation(MentorAvailability mentorAvailability, Map<String, Object> criteria) {
        double score = 0.0;

        if (criteria.containsKey("dayOfWeek") && mentorAvailability.getDayOfWeek() == criteria.get("dayOfWeek")) {
            score += 50; // Bonus for the correct day
        }

        if (criteria.containsKey("startTime") && criteria.containsKey("endTime")) {
            LocalTime requiredStartTime = (LocalTime) criteria.get("startTime");
            LocalTime requiredEndTime = (LocalTime) criteria.get("endTime");

            LocalTime availableStartTime = mentorAvailability.getStartTime();
            LocalTime availableEndTime = mentorAvailability.getEndTime();

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
    public List<MentorAvailability> filterByCriteria(List<MentorAvailability> mentorAvailabilities, Map<String, Object> criteria) {
        return mentorAvailabilities.stream()
                .filter(availability -> {
                    if (criteria.containsKey("mentorId") && !availability.getMentorId().equals(criteria.get("mentorId"))) {
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
