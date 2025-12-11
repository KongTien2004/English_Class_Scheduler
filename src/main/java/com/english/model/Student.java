package com.english.model;

import java.io.Serializable;

public class Student implements Serializable {
    public enum IELTSType {GENERAL, ACADEMIC}

    private String studentId;
    private String studentName;
    private String phone;
    private String email;
    private IELTSType ieltsType;
    private double targetBand;
    private double currentListeningBand;
    private double currentReadingBand;
    private double currentWritingBand;
    private double currentSpeakingBand;
    private String preferredCenterId;

    public Student() {
    }

    public Student(String studentId, String studentName, String phone, String email, IELTSType ieltsType, double targetBand,
                   double currentListeningBand, double currentReadingBand, double currentWritingBand, double currentSpeakingBand,
                   String preferredCenterId) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.phone = phone;
        this.email = email;
        this.ieltsType = ieltsType;
        this.targetBand = targetBand;
        this.currentListeningBand = currentListeningBand;
        this.currentReadingBand = currentReadingBand;
        this.currentWritingBand = currentWritingBand;
        this.currentSpeakingBand = currentSpeakingBand;
        this.preferredCenterId = preferredCenterId;
    }

//Getter
    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public IELTSType getIeltsType() {
        return ieltsType;
    }

    public double getTargetBand() {
        return targetBand;
    }

    public double getCurrentListeningBand() {
        return currentListeningBand;
    }

    public double getCurrentReadingBand() {
        return currentReadingBand;
    }

    public double getCurrentWritingBand() {
        return currentWritingBand;
    }

    public double getCurrentSpeakingBand() {
        return currentSpeakingBand;
    }

    public String getPreferredCenterId() {
        return preferredCenterId;
    }

//Setter
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIeltsType(IELTSType ieltsType) {
        this.ieltsType = ieltsType;
    }

    public void setTargetBand(double targetBand) {
        this.targetBand = targetBand;
    }

    public void setCurrentListeningBand(double currentListeningBand) {
        this.currentListeningBand = currentListeningBand;
    }

    public void setCurrentReadingBand(double currentReadingBand) {
        this.currentReadingBand = currentReadingBand;
    }

    public void setCurrentWritingBand(double currentWritingBand) {
        this.currentWritingBand = currentWritingBand;
    }

    public void setCurrentSpeakingBand(double currentSpeakingBand) {
        this.currentSpeakingBand = currentSpeakingBand;
    }

    public void setPreferredCenterId(String preferredCenterId) {
        this.preferredCenterId = preferredCenterId;
    }
}
