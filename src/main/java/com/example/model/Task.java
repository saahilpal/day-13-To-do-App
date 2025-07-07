package com.example.model;


public class Task {
    public String id;
    public String userId;
    public String title;
    public String description;

    public Task(String userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
    }
}
