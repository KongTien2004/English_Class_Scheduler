package com.english.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class ProgressRecord implements Serializable {
    private String progressId;
    private String studentId;
    private LocalDateTime recordedDate;
    private double listeningBand;
    private double readingBand;
    private double writingBand;
    private double speakingBand;
    private double overallBand;

    public ProgressRecord() {}

    public ProgressRecord(String progressId, String student_id, LocalDateTime recordedDate,
                          double listeningBand, double readingBand,
                          double writingBand, double speakingBand, double overallBand) {
        this.progressId = progressId;
        this.studentId = student_id;
        this.recordedDate = recordedDate;
        this.listeningBand = listeningBand;
        this.readingBand = readingBand;
        this.writingBand = writingBand;
        this.speakingBand = speakingBand;
        this.overallBand = overallBand;
    }

//Getter
    public String getProgressId() {
        return progressId;
    }

    public String getStudentId() {
        return studentId;
    }

    public LocalDateTime getRecordedDate() {
        return recordedDate;
    }

    public double getListeningBand() {
        return listeningBand;
    }

    public double getReadingBand() {
        return readingBand;
    }

    public double getWritingBand() {
        return writingBand;
    }

    public double getSpeakingBand() {
        return speakingBand;
    }

    public double getOverallBand() {
        return overallBand;
    }

//Setter
    public void setProgress_id(String progressId) {
        this.progressId = progressId;
    }

    public void setStudent_id(String studentId) {
        this.studentId = studentId;
    }

    public void setRecordedDate(LocalDateTime recorded_date) {
        this.recordedDate = recorded_date;
    }

    public void setListeningBand(double listeningBand) {
        this.listeningBand = listeningBand;
    }

    public void setReadingBand(double readingBand) {
        this.readingBand = readingBand;
    }

    public void setWritingBand(double writingBand) {
        this.writingBand = writingBand;
    }

    public void setSpeakingBand(double speakingBand) {
        this.speakingBand = speakingBand;
    }

    public void setOverallBand(double overallBand) {
        this.overallBand = overallBand;
    }
}
