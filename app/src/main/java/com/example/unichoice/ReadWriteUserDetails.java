package com.example.unichoice;

public class ReadWriteUserDetails {
    public String name,email,doB,gender,mobile;

    public ReadWriteUserDetails() {};

    public ReadWriteUserDetails(String textName,String textEmail, String textDoB, String textGender, String textMobile){
        this.name=textName;
        this.email=textEmail;
        this.doB=textDoB;
        this.gender=textGender;
        this.mobile=textMobile;
    }
}
