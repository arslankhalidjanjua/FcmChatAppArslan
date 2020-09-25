package com.example.chatappfcm.Activities.Models;

public class User {

    public String name, email;
    String uid;

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public User() {

    }

    public User(String name, String email,String uid) {
        this.name = name;
        this.uid=uid;
        this.email = email;
    }
}
