package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.Center;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CenterSearchStrategy implements SearchableEntity<Center> {
    @Override
    public double scoreCalculation(Center center, Map<String, Object> criteria) {
        double score = 0.0;

        String userLocation = (String) criteria.get("userLocation");
        if (userLocation == null) score += locationScoreCalculation(center.getAddress(), userLocation);

        score += completenessScoreCalculation(center);
        score += nameQualityScoreCalculation(center.getCenterName());

        return score;
    }

    @Override
    public List<Center> filterByCriteria(List<Center> centers, Map<String, Object> criteria) {
        String location = (String) criteria.get("userLocation");
        if (location == null) return centers;

        return centers.stream()
                .filter(center -> center.getAddress() != null &&
                        center.getAddress().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }

    private double locationScoreCalculation(String address, String userLocation) {
        if (address == null || userLocation == null) return 0.0;

        String lowerAddress = address.toLowerCase();
        String lowerUserLocation = userLocation.toLowerCase();

        if (lowerAddress.contains(lowerUserLocation)) return 50.0;

        int distance = levenshteinDistance(lowerAddress, lowerUserLocation);
        return Math.max(0, 30 - distance * 0.5);
    }

    private int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= str2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[str1.length()][str2.length()];
    }

    private double completenessScoreCalculation(Center center) {
        double score = 0.0;

        if (center.getCenterId() != null && !center.getCenterId().trim().isEmpty()) score += 5.0;
        if (center.getCenterName() != null && !center.getCenterName().trim().isEmpty()) score += 10.0;
        if (center.getAddress() != null && !center.getAddress().trim().isEmpty()) score += 5.0;

        return score;
    }

    private double nameQualityScoreCalculation(String centerName) {
        if (centerName == null) return 0.0;
        int length = centerName.trim().length();
        if (length >= 10 && length <= 50) return 30.0;
        if (length > 50) return 15.0;
        return 10.0;
    }
}
