package com.imaginers.onirban.home.Auth.Model;

public class User {
    private String name;
    private String phone;

    public User(String pName, String pPhone) {
        name = pName;
        phone = pPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String pPhone) {
        phone = pPhone;
    }
}
