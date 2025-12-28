package com.english.model;

import java.io.Serializable;

public class Center implements Serializable {
    private String centerId;
    private String centerName;
    private String address;
    private String city;

    public Center() {}

    public Center(String centerId, String centerName, String address, String city) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.address = address;
        this.city = city;
    }

//Getter
    public String getCenterId() {
        return centerId;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    //Setter
    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
