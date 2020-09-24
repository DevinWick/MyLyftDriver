package com.phantomarts.mylyftdriver.model;

public class Driver {
    public static final int ACCOUNT_COMPLETE_STAGE1=1; //phone verification
    public static final int ACCOUNT_COMPLETE_STAGE2=2;
    public static final int ACCOUNT_COMPLETE_STAGE3=3; //add nic
    public static final int ACCOUNT_COMPLETE_STAGE4=4; //add vehicle details
    public static final int ACCOUNT_COMPLETE_STAGE5=5; //add profile pic
    public static final int ACCOUNT_COMPLETE_ALL=0; // registration completed


    private String email;
    private String password;
    private String phoneNo;

    private String firstName;
    private String lastName;
    private String nic1;
    private String nic2;
    private String profilePic;
    private String vid;
    private int regCompleteStaus;
    private boolean isOnline=false;

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Driver() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNic1() {
        return nic1;
    }

    public void setNic1(String nic1) {
        this.nic1 = nic1;
    }

    public String getNic2() {
        return nic2;
    }

    public void setNic2(String nic2) {
        this.nic2 = nic2;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public int getRegCompleteStaus() {
        return regCompleteStaus;
    }

    public void setRegCompleteStaus(int regCompleteStaus) {
        this.regCompleteStaus = regCompleteStaus;
    }
}
