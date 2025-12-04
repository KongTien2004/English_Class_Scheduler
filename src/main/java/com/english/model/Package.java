package com.english.model;

public class Package {
    public enum IELTSType {GENERAL, ACADEMIC}

    private String packageId;
    private String packageName;
    private IELTSType ieltsType;
    private double targetBand;
    private int totalSessions;
    private double price;
    private boolean isActive;

    public Package() {}

    public Package(String packageId, String packageName, IELTSType ieltsType,
                   double targetBand, int totalSessions, double price, boolean isActive) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.ieltsType = ieltsType;
        this.targetBand = targetBand;
        this.totalSessions = totalSessions;
        this.price = price;
        this.isActive = isActive;
    }

//Getter
    public String getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
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

    public double getPrice() {
        return price;
    }

    public boolean isActive() {
        return isActive;
    }

//Setter
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
