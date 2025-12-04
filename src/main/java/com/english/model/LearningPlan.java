package com.english.model;

import java.time.LocalDate;
import java.util.Date;

public class LearningPlan {
    public enum IELTSType {GENERAL, ACADEMIC}
    public enum PlanStatus {ACTIVE, PAUSED, COMPLETED, CANCELLED}

    private String planId;
    private String studentId;
    private String mentorId;
    private IELTSType ieltsType;
    private double targetBand;
    private int totalSessions;
    private int remainingSessions;
    private LocalDate startDate;
    private PlanStatus planStatus;
    private Date createdDate;

    public LearningPlan() {}

    public LearningPlan(String planId, String studentId, String mentorId, IELTSType ieltsType,
                        double targetBand, int totalSessions, int remainingSessions,
                        LocalDate startDate, PlanStatus planStatus, Date createdDate) {
        this.planId = planId;
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.ieltsType = ieltsType;
        this.targetBand = targetBand;
        this.totalSessions = totalSessions;
        this.remainingSessions = remainingSessions;
        this.startDate = startDate;
        this.planStatus = planStatus;
        this.createdDate = createdDate;
    }

//Getter
    public String getPlanId() {
        return planId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getMentorId() {
        return mentorId;
    }

    public IELTSType getIeltsType() {
        return ieltsType;
    }

    public double getTargetBand() {
        return targetBand;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public int getRemainingSessions() {
        return remainingSessions;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public PlanStatus getPlanStatus() {
        return planStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

//Setter
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public void setIeltsType(IELTSType ieltsType) {
        this.ieltsType = ieltsType;
    }

    public void setTargetBand(double targetBand) {
        this.targetBand = targetBand;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public void setRemainingSessions(int remainingSessions) {
        this.remainingSessions = remainingSessions;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setPlanStatus(PlanStatus planStatus) {
        this.planStatus = planStatus;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
