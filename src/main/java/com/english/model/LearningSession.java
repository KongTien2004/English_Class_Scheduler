package com.english.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LearningSession implements Serializable {
    public enum SessionType {Online, Offline}
    public enum SessionStatus {scheduled, completed, cancelled, no_show}

    private String sessionId;
    private String planId;
    private int sessionNumber;
    private SessionType sessionType;
    private LocalDateTime scheduledTime;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private SessionStatus sessionStatus;

    public LearningSession() {}

    public LearningSession(String sessionId, String planId, int sessionNumber, SessionType sessionType,
                           LocalDateTime scheduledTime, LocalTime startTime, LocalTime endTime,
                           String location, SessionStatus sessionStatus) {
        this.sessionId = sessionId;
        this.planId = planId;
        this.sessionNumber = sessionNumber;
        this.sessionType = sessionType;
        this.scheduledTime = scheduledTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.sessionStatus = sessionStatus;
    }

//Getter
    public String getSessionId() {
        return sessionId;
    }

    public String getPlanId() {
        return planId;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

//Setter
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}
