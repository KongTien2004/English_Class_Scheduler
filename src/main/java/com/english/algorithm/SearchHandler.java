package com.english.algorithm;

import com.english.algorithm.strategy.CenterSearchStrategy;
import com.english.algorithm.strategy.LearningSessionSearchStrategy;
import com.english.algorithm.strategy.MentorSearchStrategy;
import com.english.algorithm.strategy.StudentSearchStrategy;
import com.english.model.Center;
import com.english.model.LearningSession;
import com.english.model.Mentor;
import com.english.model.Student;

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
}
