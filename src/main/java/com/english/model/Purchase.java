package com.english.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Purchase implements Serializable {
    private String purchaseId;
    private String studentId;
    private String packageId;
    private LocalDate purchasedDate;
    private double amountPaid;

    public Purchase() {}

    public Purchase(String purchaseId, String studentId, String packageId, LocalDate purchasedDate, double amountPaid) {
        this.purchaseId = purchaseId;
        this.studentId = studentId;
        this.packageId = packageId;
        this.purchasedDate = purchasedDate;
        this.amountPaid = amountPaid;
    }

//Getter
    public String getPurchaseId() {
        return purchaseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPackageId() {
        return packageId;
    }

    public LocalDate getPurchasedDate() {
        return purchasedDate;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

//Setter
    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public void setPurchasedDate(LocalDate purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }
}
