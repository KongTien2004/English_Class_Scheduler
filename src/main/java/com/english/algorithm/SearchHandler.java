package com.english.hill_climbing;

import com.english.hill_climbing.strategy.CenterSearchStrategy;
import com.english.hill_climbing.strategy.MentorSearchStrategy;
import com.english.hill_climbing.strategy.StudentSearchStrategy;
import com.english.model.Center;
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
}
