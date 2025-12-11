package com.english.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LearningSession implements Serializable {
    public enum SessionType {ONLINE, OFFLINE}
    public enum SessionStatus {SCHEDULED, COMPLETED, CANCELLED, NO_SHOW}

    private String sessionId;
    private String planId;
    private int sessionNumber;
    private SessionType sessionType;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String location;
    private SessionStatus sessionStatus;

    public LearningSession() {}

    public LearningSession(String sessionId, String planId, int sessionNumber, SessionType sessionType,
                           LocalDateTime scheduledTime, LocalDateTime actualStart, LocalDateTime actualEnd,
                           String location, SessionStatus sessionStatus) {
        this.sessionId = sessionId;
        this.planId = planId;
        this.sessionNumber = sessionNumber;
        this.sessionType = sessionType;
        this.scheduledTime = scheduledTime;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
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

    public LocalDateTime getActualStart() {
        return actualStart;
    }

    public LocalDateTime getActualEnd() {
        return actualEnd;
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

    public void setActualStart(LocalDateTime actualStart) {
        this.actualStart = actualStart;
    }

    public void setActualEnd(LocalDateTime actualEnd) {
        this.actualEnd = actualEnd;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}
