package com.english.algorithm;

import java.util.*;
import java.util.stream.Collectors;

public class OptimizedSearchHandler<T> {
    private final Random random;
    private final SearchableEntity<T> searchStrategy;
    private static final int DEFAULT_MAX_ITERATIONS = 100;
    private static final int NEIGHBOUR_SIZE = 15;

    public OptimizedSearchHandler() {
    }

    public OptimizedSearchHandler(SearchableEntity<T> searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public OptimizedSearchHandler(Random random, SearchableEntity<T> searchStrategy) {
        this.random = random;
        this.searchStrategy = searchStrategy;
    }

    public T findOptimalEntity(List<T> entities, Map<String, Object> criteria) {
        return findOptimalEntity(entities, criteria, DEFAULT_MAX_ITERATIONS);
    }

    public T findOptimalEntity(List<T> entities, Map<String, Object> criteria, int maxIterations) {
        if (entities == null || entities.isEmpty()) return null;

        List<T> filteredEntities = searchStrategy.filterByCriteria(entities, criteria);
        if (filteredEntities.isEmpty()) filteredEntities = entities;

        T currentEntity = filteredEntities.get(random.nextInt(filteredEntities.size()));
        double currentScore = searchStrategy.scoreCalculation(currentEntity, criteria);

        int noImprovements = 0;
        for (int i = 0; i < maxIterations && noImprovements < 10; i++) {
            List<T> neighbors = getRandomNeighbor(filteredEntities, currentEntity, Math.min(NEIGHBOUR_SIZE, filteredEntities.size()));

            T bestNeighbor = null;
            double bestNeighborScore = currentScore;

            for (T neighbor : neighbors) {
                double score = searchStrategy.scoreCalculation(neighbor, criteria);
                if (score > bestNeighborScore) {
                    bestNeighbor = neighbor;
                    bestNeighborScore = score;
                }
            }

            if (bestNeighbor != null) {
                noImprovements++;
            } else {
                currentEntity = bestNeighbor;
                currentScore = bestNeighborScore;
                noImprovements = 0;
            }
        }

        return currentEntity;
    }

    public List<T> getTopEntities(List<T> entities, Map<String, Object> criteria, int topEntities) {
        if (entities == null || entities.isEmpty()) return null;

        List<T> filteredEntities = searchStrategy.filterByCriteria(entities, criteria);
        if (filteredEntities.isEmpty()) filteredEntities = entities;

        return filteredEntities.stream()
                .map(entity -> new ScoredEntity<>(entity,
                        searchStrategy.scoreCalculation(entity, criteria)))
                .sorted(Comparator.comparingDouble(ScoredEntity::getScore).reversed())
                .limit(topEntities)
                .map(ScoredEntity::getEntity)
                .collect(Collectors.toList());
    }

    private List<T> getRandomNeighbor(List<T> filteredEntities, T currentEntity, int neighborCount) {
        List<T> neighbors = new ArrayList<>(filteredEntities);
        neighbors.remove(currentEntity);
        Collections.shuffle(neighbors, random);
        return neighbors.subList(0, Math.min(neighborCount, neighbors.size()));
    }

    //Subclass
    private static class ScoredEntity<T> {
        private final T entity;
        private final double score;

        public ScoredEntity(T entity, double score) {
            this.entity = entity;
            this.score = score;
        }

        public T getEntity() {
            return entity;
        }
        public double getScore() {
            return score;
        }
    }
}
