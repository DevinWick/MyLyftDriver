package com.phantomarts.mylyftdriver.model;

public class Vehicle {
    private String owner;
    private String regNo;
    private String brand;
    private String model;
    private String color;
    private String type;
    private boolean isOwner;

    public Vehicle() {
    }

    public Vehicle(String owner, String regNo, String brand, String model, String color, String type, boolean isOwner, String uid) {
        this.owner = owner;
        this.regNo = regNo;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.type = type;
        this.isOwner = isOwner;
        this.uid = uid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

}
