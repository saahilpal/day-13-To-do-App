package com.example.model;


public class User {
    public String id;
    public String email;
    public String name;
    public String hashedPassword;

    public User(String email, String name, String hashedPassword) {
        this.email = email;
        this.name = name;
        this.hashedPassword = hashedPassword;
    }
}
