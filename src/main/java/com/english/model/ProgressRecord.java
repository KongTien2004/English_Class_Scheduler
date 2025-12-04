package com.english.model;

import java.util.Date;

public class ProgressRecord {
    private String progress_id;
    private String student_id;
    private Date recorded_date;
    private double listeningBand;
    private double readingBand;
    private double writingBand;
    private double speakingBand;
    private double overallBand;

    public ProgressRecord() {}

    public ProgressRecord(String progress_id, String student_id, Date recorded_date,
                          double listeningBand, double readingBand,
                          double writingBand, double speakingBand, double overallBand) {
        this.progress_id = progress_id;
        this.student_id = student_id;
        this.recorded_date = recorded_date;
        this.listeningBand = listeningBand;
        this.readingBand = readingBand;
        this.writingBand = writingBand;
        this.speakingBand = speakingBand;
        this.overallBand = overallBand;
    }

//Getter
    public String getProgress_id() {
        return progress_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public Date getRecorded_date() {
        return recorded_date;
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
    public void setProgress_id(String progress_id) {
        this.progress_id = progress_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public void setRecorded_date(Date recorded_date) {
        this.recorded_date = recorded_date;
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
