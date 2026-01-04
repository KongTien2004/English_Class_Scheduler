package com.english.model;

import java.io.Serializable;

public class Assistant implements Serializable {
    private String assistantId;
    private String assistantName;
    private String email;
    private double certifiedBand;
    private boolean strongReading;
    private boolean strongListening;
    private boolean strongWriting;
    private boolean strongSpeaking;
    private boolean canSupportGeneral;
    private boolean canSupportAcademic;
    private boolean isAvailable;
    private String assistantAddress;

    public Assistant() {
    }

    public Assistant(String assistantId, String assistantName, String email, double certifiedBand,
                     boolean strongReading, boolean strongListening, boolean strongWriting, boolean strongSpeaking,
                     boolean canSupportGeneral, boolean canSupportAcademic, boolean isAvailable, String assistantAddress) {
        this.assistantId = assistantId;
        this.assistantName = assistantName;
        this.email = email;
        this.certifiedBand = certifiedBand;
        this.strongReading = strongReading;
        this.strongListening = strongListening;
        this.strongWriting = strongWriting;
        this.strongSpeaking = strongSpeaking;
        this.canSupportGeneral = canSupportGeneral;
        this.canSupportAcademic = canSupportAcademic;
        this.isAvailable = isAvailable;
        this.assistantAddress = assistantAddress;
    }

//Getter
    public String getAssistantId() {
        return assistantId;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public String getEmail() {
        return email;
    }

    public double getCertifiedBand() {
        return certifiedBand;
    }

    public boolean isStrongReading() {
        return strongReading;
    }

    public boolean isStrongListening() {
        return strongListening;
    }

    public boolean isStrongWriting() {
        return strongWriting;
    }

    public boolean isStrongSpeaking() {
        return strongSpeaking;
    }

    public boolean isCanSupportGeneral() {
        return canSupportGeneral;
    }

    public boolean isCanSupportAcademic() {
        return canSupportAcademic;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getAssistantAddress() {
        return assistantAddress;
    }

    //Setter
    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCertifiedBand(double certified_band) {
        this.certifiedBand = certified_band;
    }

    public void setStrongReading(boolean strongReading) {
        this.strongReading = strongReading;
    }

    public void setStrongListening(boolean strongListening) {
        this.strongListening = strongListening;
    }

    public void setStrongWriting(boolean strongWriting) {
        this.strongWriting = strongWriting;
    }

    public void setStrongSpeaking(boolean strongSpeaking) {
        this.strongSpeaking = strongSpeaking;
    }

    public void setCanSupportGeneral(boolean canSupportGeneral) {
        this.canSupportGeneral = canSupportGeneral;
    }

    public void setCanSupportAcademic(boolean canSupportAcademic) {
        this.canSupportAcademic = canSupportAcademic;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setAssistantAddress(String assistantAddress) {
        this.assistantAddress = assistantAddress;
    }
}
