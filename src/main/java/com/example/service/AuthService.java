package com.example.service;

import com.example.model.User;

import java.util.*;

public class AuthService {
    private final Map<String, User> users = new HashMap<>();

    public void register(String email, String name) {
        if (users.containsKey(email)) {
            System.out.println("User already exists.");
            return;
        }
        String password = UUID.randomUUID().toString().substring(0, 8);
        String hashed = PasswordUtil.hash(password);
        User user = new User(email, name, hashed);
        users.put(email, user);
        System.out.println("Registered. Your password: " + password);
    }

    public String login(String email, String password) {
        User user = users.get(email);
        if (user != null && PasswordUtil.verify(password, user.hashedPassword)) {
            System.out.println("Login successful.");
            return email;
        }
        System.out.println("Login failed.");
        return null;
    }
}
