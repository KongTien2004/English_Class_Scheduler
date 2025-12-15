package com.english.model;

import java.io.Serializable;
import java.time.LocalTime;

public class MentorAvailability implements Serializable {
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
    private String mentorId;
    private DayOfWeeks dayOfWeek;
    private LocalTime startTime, endTime;

    public MentorAvailability() {
    }

    public MentorAvailability(String availabilityId, String mentorId, DayOfWeeks dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.availabilityId = availabilityId;
        this.mentorId = mentorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

//Getter
    public String getAvailabilityId() {
        return availabilityId;
    }

    public String getMentorId() {
        return mentorId;
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

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
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
