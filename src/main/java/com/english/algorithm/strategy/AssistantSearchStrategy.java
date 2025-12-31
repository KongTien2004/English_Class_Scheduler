package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.Assistant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssistantSearchStrategy implements SearchableEntity<Assistant> {
    @Override
    public double scoreCalculation(Assistant assistant, Map<String, Object> criteria) {
        double score = 0;

        // Score based on certified band
        if (criteria.containsKey("certified_band")) {
            double desiredBand = (double) criteria.get("certified_band");
            if (assistant.getCertified_band() >= desiredBand) {
                score += 20;
            }
        }

        // Score based on skills
        if (criteria.containsKey("strongReading") && (boolean) criteria.get("strongReading") && assistant.isStrongReading()) {
            score += 10;
        }
        if (criteria.containsKey("strongListening") && (boolean) criteria.get("strongListening") && assistant.isStrongListening()) {
            score += 10;
        }
        if (criteria.containsKey("strongWriting") && (boolean) criteria.get("strongWriting") && assistant.isStrongWriting()) {
            score += 10;
        }
        if (criteria.containsKey("strongSpeaking") && (boolean) criteria.get("strongSpeaking") && assistant.isStrongSpeaking()) {
            score += 10;
        }

        // Score based on support type
        if (criteria.containsKey("canSupportGeneral") && (boolean) criteria.get("canSupportGeneral") && assistant.isCanSupportGeneral()) {
            score += 15;
        }
        if (criteria.containsKey("canSupportAcademic") && (boolean) criteria.get("canSupportAcademic") && assistant.isCanSupportAcademic()) {
            score += 15;
        }

        if (assistant.isAvailable()) {
            score += 10;
        }

        return score;
    }

    @Override
    public List<Assistant> filterByCriteria(List<Assistant> assistants, Map<String, Object> criteria) {
        return assistants.stream()
                .filter(assistant -> {
                    if (criteria.containsKey("assistantId") && !assistant.getAssistantId().equals(criteria.get("assistantId"))) {
                        return false;
                    }
                    if (criteria.containsKey("assistantName") && !assistant.getAssistantName().toLowerCase().contains(((String) criteria.get("assistantName")).toLowerCase())) {
                        return false;
                    }
                    if (criteria.containsKey("email") && !assistant.getEmail().toLowerCase().contains(((String) criteria.get("email")).toLowerCase())) {
                        return false;
                    }
                    if (criteria.containsKey("certified_band") && assistant.getCertified_band() < (double) criteria.get("certified_band")) {
                        return false;
                    }
                    if (criteria.containsKey("strongReading") && (boolean) criteria.get("strongReading") && !assistant.isStrongReading()) {
                        return false;
                    }
                    if (criteria.containsKey("strongListening") && (boolean) criteria.get("strongListening") && !assistant.isStrongListening()) {
                        return false;
                    }
                    if (criteria.containsKey("strongWriting") && (boolean) criteria.get("strongWriting") && !assistant.isStrongWriting()) {
                        return false;
                    }
                    if (criteria.containsKey("strongSpeaking") && (boolean) criteria.get("strongSpeaking") && !assistant.isStrongSpeaking()) {
                        return false;
                    }
                    if (criteria.containsKey("canSupportGeneral") && (boolean) criteria.get("canSupportGeneral") && !assistant.isCanSupportGeneral()) {
                        return false;
                    }
                    if (criteria.containsKey("canSupportAcademic") && (boolean) criteria.get("canSupportAcademic") && !assistant.isCanSupportAcademic()) {
                        return false;
                    }
                    if (criteria.containsKey("isAvailable") && (boolean) criteria.get("isAvailable") != assistant.isAvailable()) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
