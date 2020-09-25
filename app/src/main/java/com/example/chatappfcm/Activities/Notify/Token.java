package com.example.chatappfcm.Activities.Notify;

public class Token {
    private String token;

    //constructors

    public Token(String token) {this.token = token; }
    public Token() {    }


    //setters and getters

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
