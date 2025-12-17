package com.english.algorithm;

import com.english.algorithm.strategy.*;
import com.english.model.*;
import com.english.model.Package;

public class SearchHandler {
    public static OptimizedSearchHandler<Center> centerHandler() {
        return new OptimizedSearchHandler<>(new CenterSearchStrategy());
    }

    public static OptimizedSearchHandler<Mentor> mentorHandler() {
        return new OptimizedSearchHandler<>(new MentorSearchStrategy());
    }

    public static OptimizedSearchHandler<Student> studentHandler() {
        return new OptimizedSearchHandler<>(new StudentSearchStrategy());
    }

    public static OptimizedSearchHandler<LearningSession> learningSessionHandler() {
        return new OptimizedSearchHandler<>(new LearningSessionSearchStrategy());
    }

    public static OptimizedSearchHandler<Room> roomSearchHandler() {
        return new OptimizedSearchHandler<>(new RoomSearchStrategy());
    }

    public static OptimizedSearchHandler<Package> packageSearchHandler() {
        return new OptimizedSearchHandler<>(new PackageSearchStrategy());
    }

    public static OptimizedSearchHandler<LearningPlan> learningPlanSearchHandler() {
        return new OptimizedSearchHandler<>(new LearningPlanSearchStrategy());
    }
}
