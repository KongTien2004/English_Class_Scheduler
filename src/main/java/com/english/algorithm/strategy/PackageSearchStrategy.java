package com.english.algorithm.strategy;

import com.english.algorithm.SearchableEntity;
import com.english.model.Package;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageSearchStrategy implements SearchableEntity<Package> {

    /**
     * Calculates a score for a package based on a given set of criteria.
     * The score indicates how well the package matches the criteria.
     *
     * @param entity   The package to be scored.
     * @param criteria A map of criteria to score against. Keys can be "targetBand", "price", "totalSessions".
     * @return A double representing the score of the package.
     */
    @Override
    public double scoreCalculation(Package entity, Map<String, Object> criteria) {
        double score = 1.0;

        // Score based on target band
        if (criteria.containsKey("targetBand") && criteria.get("targetBand") instanceof Number) {
            double desiredBand = ((Number) criteria.get("targetBand")).doubleValue();
            double entityBand = entity.getTargetBand();
            if (entityBand >= desiredBand) {
                // Bonus for exceeding the target
                score += (entityBand - desiredBand) * 0.1;
            } else {
                // Penalize if it doesn't meet the target
                score -= (desiredBand - entityBand) * 0.2;
            }
        }

        // Score based on price (lower is better)
        if (criteria.containsKey("price") && criteria.get("price") instanceof Number) {
            double desiredPrice = ((Number) criteria.get("price")).doubleValue();
            if (desiredPrice > 0) {
                double entityPrice = entity.getPrice();
                // Score is higher for lower prices.
                score += (desiredPrice - entityPrice) / desiredPrice;
            }
        }

        // Score based on total sessions
        if (criteria.containsKey("totalSessions") && criteria.get("totalSessions") instanceof Number) {
            int desiredSessions = ((Number) criteria.get("totalSessions")).intValue();
            int entitySessions = entity.getTotalSessions();
            if (entitySessions >= desiredSessions) {
                score += 0.2;
            }
        }

        return Math.max(0, score); // Ensure score is non-negative
    }

    /**
     * Filters a list of packages based on a given set of criteria.
     *
     * @param entities The list of packages to filter.
     * @param criteria A map of criteria to filter by. Keys can include "isActive", "ieltsType",
     *                 "minTargetBand", "maxTargetBand", "minPrice", "maxPrice",
     *                 "minTotalSessions", "maxTotalSessions".
     * @return A list of packages that match the criteria.
     */
    @Override
    public List<Package> filterByCriteria(List<Package> entities, Map<String, Object> criteria) {
        Stream<Package> stream = entities.stream();

        // Filter by active status
        if (criteria.containsKey("isActive") && criteria.get("isActive") instanceof Boolean) {
            stream = stream.filter(p -> p.isActive() == (boolean) criteria.get("isActive"));
        } else {
            stream = stream.filter(Package::isActive); // Default to active packages
        }

        // Filter by IELTS type
        if (criteria.containsKey("ieltsType") && criteria.get("ieltsType") instanceof Package.IELTSType) {
            Package.IELTSType ieltsType = (Package.IELTSType) criteria.get("ieltsType");
            stream = stream.filter(p -> p.getIeltsType() == ieltsType);
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

        // Filter by price range
        if (criteria.containsKey("minPrice") && criteria.get("minPrice") instanceof Number) {
            double minPrice = ((Number) criteria.get("minPrice")).doubleValue();
            stream = stream.filter(p -> p.getPrice() >= minPrice);
        }
        if (criteria.containsKey("maxPrice") && criteria.get("maxPrice") instanceof Number) {
            double maxPrice = ((Number) criteria.get("maxPrice")).doubleValue();
            stream = stream.filter(p -> p.getPrice() <= maxPrice);
        }

        // Filter by total sessions range
        if (criteria.containsKey("minTotalSessions") && criteria.get("minTotalSessions") instanceof Number) {
            int minSessions = ((Number) criteria.get("minTotalSessions")).intValue();
            stream = stream.filter(p -> p.getTotalSessions() >= minSessions);
        }
        if (criteria.containsKey("maxTotalSessions") && criteria.get("maxTotalSessions") instanceof Number) {
            int maxSessions = ((Number) criteria.get("maxTotalSessions")).intValue();
            stream = stream.filter(p -> p.getTotalSessions() <= maxSessions);
        }

        return stream.collect(Collectors.toList());
    }
}