package com.english.model;

import java.io.Serializable;
import java.time.LocalTime;

public class StudentAvailability implements Serializable {
    public enum DayOfWeeks {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    private String availabilityId;
    private String studentId;
    private DayOfWeeks dayOfWeek;
    private LocalTime startTime, endTime;

    public StudentAvailability() {
    }

    public StudentAvailability(String availabilityId, String studentId, DayOfWeeks dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.availabilityId = availabilityId;
        this.studentId = studentId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Getter
    public String getAvailabilityId() {
        return availabilityId;
    }

    public String getStudentId() {
        return studentId;
    }

    public DayOfWeeks getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    //Setter
    public void setAvailabilityId(String availabilityId) {
        this.availabilityId = availabilityId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setDayOfWeek(DayOfWeeks dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
