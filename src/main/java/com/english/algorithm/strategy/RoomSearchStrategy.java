package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.Room;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomSearchStrategy implements SearchableEntity<Room> {
    @Override
    public double scoreCalculation(Room room, Map<String, Object> criteria) {
        if (!room.isAvailable()) {
            return 0.0;
        }

        double score = 0.0;

        // Score based on capacity
        Integer requiredCapacity = (Integer) criteria.get("required_capacity");
        if (requiredCapacity != null) {
            if (room.getCapacity() >= requiredCapacity) {
                // Higher score for a closer capacity match
                score += 50.0 / (1 + (room.getCapacity() - requiredCapacity));
            }
        }

        // Score based on center
        String centerId = (String) criteria.get("centerId");
        if (centerId != null && centerId.equals(room.getCenterId())) {
            score += 50.0;
        }

        return score;
    }

    @Override
    public List<Room> filterByCriteria(List<Room> rooms, Map<String, Object> criteria) {
        String centerId = (String) criteria.get("centerId");
        Integer minCapacity = (Integer) criteria.get("min_capacity");

        return rooms.stream()
                .filter(Room::isAvailable)
                .filter(room -> centerId == null || room.getCenterId().equals(centerId))
                .filter(room -> minCapacity == null || room.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }
}
