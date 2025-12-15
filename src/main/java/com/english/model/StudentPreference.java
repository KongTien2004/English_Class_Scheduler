package com.english.model;

import java.io.Serializable;
import java.time.LocalTime;

public class StudentPreference implements Serializable {
    public enum DayOfWeeks {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    private String preferenceId;
    private String studentId;
    private String preferredCenter;
    private DayOfWeeks dayOfWeek;
    private LocalTime preferredStart, preferredEnd;

    public StudentPreference() {
    }

    public StudentPreference(String preferenceId, String studentId, String preferredCenter, DayOfWeeks dayOfWeek, LocalTime preferredStart, LocalTime preferredEnd) {
        this.preferenceId = preferenceId;
        this.studentId = studentId;
        this.preferredCenter = preferredCenter;
        this.dayOfWeek = dayOfWeek;
        this.preferredStart = preferredStart;
        this.preferredEnd = preferredEnd;
    }

//Getter
    public String getPreferenceId() {
        return preferenceId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPreferredCenter() {
        return preferredCenter;
    }

    public DayOfWeeks getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getPreferredStart() {
        return preferredStart;
    }

    public LocalTime getPreferredEnd() {
        return preferredEnd;
    }

//Setter
    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setPreferredCenter(String preferredCenter) {
        this.preferredCenter = preferredCenter;
    }

    public void setDayOfWeek(DayOfWeeks dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setPreferredStart(LocalTime preferredStart) {
        this.preferredStart = preferredStart;
    }

    public void setPreferredEnd(LocalTime preferredEnd) {
        this.preferredEnd = preferredEnd;
    }
}
