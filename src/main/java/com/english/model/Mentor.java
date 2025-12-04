package com.english.model;

public class Mentor {
    private String mentorId;
    private String mentorName;
    private String email;
    private double certifiedBand;
    private boolean canTeachGeneral;
    private boolean canTeachAcademic;
    private boolean isAvailable;

    public Mentor() {}

    public Mentor(String mentorId, String mentorName, String email, double certifiedBand,
                  boolean canTeachGeneral, boolean canTeachAcademic, boolean isAvailable) {
        this.mentorId = mentorId;
        this.mentorName = mentorName;
        this.email = email;
        this.certifiedBand = certifiedBand;
        this.canTeachGeneral = canTeachGeneral;
        this.canTeachAcademic = canTeachAcademic;
        this.isAvailable = isAvailable;
    }

//Getter
    public String getMentorId() {
        return mentorId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public String getEmail() {
        return email;
    }

    public double getCertifiedBand() {
        return certifiedBand;
    }

    public boolean isCanTeachGeneral() {
        return canTeachGeneral;
    }

    public boolean isCanTeachAcademic() {
        return canTeachAcademic;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

//Setter
    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCertifiedBand(double certifiedBand) {
        this.certifiedBand = certifiedBand;
    }

    public void setCanTeachGeneral(boolean canTeachGeneral) {
        this.canTeachGeneral = canTeachGeneral;
    }

    public void setCanTeachAcademic(boolean canTeachAcademic) {
        this.canTeachAcademic = canTeachAcademic;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
