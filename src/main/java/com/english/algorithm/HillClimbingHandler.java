package com.english.algorithm;

import com.english.model.Center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class HillClimbingHandler {
    private Random random;

    public HillClimbingHandler(Random random) {
        this.random = random;
    }

    public Center findOptimalCenter(List<Center> centers, String userLocation, int maxIterations) {
        if (centers == null || centers.isEmpty()) return null;

        Center currentBestCenter = centers.get(random.nextInt(centers.size()));
        double currentScore = evaluateCenter(currentBestCenter, userLocation);

        for (int i = 0; i < maxIterations; i++) {
            List<Center> centerNeighbors = getNeighbors(centers, currentBestCenter);

            if (centerNeighbors.isEmpty()) break;

            Center bestNeighbor = null;
            double bestNeighborScore = currentScore;

            for (Center neighbor : centerNeighbors) {
                double score = evaluateCenter(neighbor, userLocation);
                if (score > bestNeighborScore) {
                    bestNeighbor = neighbor;
                    bestNeighborScore = score;
                }
            }

            if (bestNeighbor == null) break;

            currentBestCenter = bestNeighbor;
            currentScore = bestNeighborScore;
        }

        return currentBestCenter;
    }

    private double evaluateCenter(Center currentBestCenter, String userLocation) {
        double score = 0.0;
        score += calculateDistanceScore(currentBestCenter.getAddress(), userLocation);
        score += calculateNameScore(currentBestCenter.getCenterName());
        score += calculateCompleteness(currentBestCenter);

        return score;
    }

    private double calculateDistanceScore(String address, String userLocation) {
        if (address == null || userLocation == null) return 0.0;

        if (address.toLowerCase().contains(userLocation.toLowerCase())) return 50.0;

        int distance = levenshteinDistance(address.toLowerCase(), userLocation.toLowerCase());
        return Math.max(0, 30 - distance * 50);
    }

    private double calculateNameScore(String centerName) {
        if (centerName == null || centerName.trim().isEmpty()) return 0.0;

        int nameLength = centerName.length();
        if (nameLength >= 10 && nameLength <= 50) {
            return 30.0;
        } else if (nameLength > 50) {
            return 15.0;
        } else {
            return 10.0;
        }
    }

    private double calculateCompleteness(Center currentBestCenter) {
        double score = 0.0;

        if (currentBestCenter.getCenterId() != null && !currentBestCenter.getCenterId().trim().isEmpty()) score += 5.0;
        if (currentBestCenter.getCenterName() != null && !currentBestCenter.getCenterName().trim().isEmpty()) score += 10.0;
        if (currentBestCenter.getAddress() != null && !currentBestCenter.getAddress().trim().isEmpty()) score += 5.0;

        return score;
    }

    private List<Center> getNeighbors(List<Center> centers, Center currentBestCenter) {
        List<Center> neighbors = new ArrayList<>();

        for (Center center : centers) {
            if (!center.getCenterId().equals(currentBestCenter.getCenterId())) {
                neighbors.add(center);
            }
        }

        return neighbors;
    }

    private int levenshteinDistance(String lowerCase1, String lowerCase2) {
        int[][] dp = new int[lowerCase1.length() + 1][lowerCase2.length() + 1];

        for (int i = 0; i <= lowerCase1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= lowerCase2.length(); j++) dp[0][j] = j;

        for (int a = 1; a < lowerCase1.length(); a++) {
            for (int b = 0; b < lowerCase2.length(); b++) {
                int cost = (lowerCase1.charAt(a - 1) == lowerCase2.charAt(b - 1)) ? 0 : 1;
                dp[a][b] = Math.min(Math.min(
                        dp[a - 1][b] + 1,
                        dp[a][b - 1] + 1),
                        dp[a - 1][b - 1] + cost
                );
            }
        }

        return dp[lowerCase1.length()][lowerCase2.length()];
    }

    public List<Center> getTopCenters(List<Center> centers, String userLocation, int top) {
        List<Center> topCenters = new ArrayList<>();

        topCenters.sort((c1, c2) -> {
            double c1Score = evaluateCenter(c1, userLocation);
            double c2Score = evaluateCenter(c2, userLocation);
            return Double.compare(c1Score, c2Score);
        });

        return topCenters.subList(0, Math.min(top, topCenters.size()));
    }
}
