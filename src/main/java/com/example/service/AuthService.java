package com.example.service;

import com.example.security.JwtProvider;
import com.example.security.PasswordHasher;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;


import java.time.Duration;
import java.util.List;

public class AuthService {

    private final MongoClient mongo;
    private final RedisAPI redis;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    public AuthService(MongoClient mongo, RedisAPI redis, JwtProvider jwtProvider, EmailService emailService) {
        this.mongo = mongo;
        this.redis = redis;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
    }

    public void login(String email, String password) {
        JsonObject query = new JsonObject().put("email", email).put("type", "user");

        mongo.findOne("data", query, null).onSuccess(user -> {
            if (user == null) {
                System.out.println("Invalid credentials");
                return;
            }

            String hashed = user.getString("hashedPassword");
            if (!PasswordHasher.verify(password, hashed)) {
                System.out.println("Invalid password");
                return;
            }

            String userId = user.getString("_id");
            String token = jwtProvider.generateToken(userId);

            // Store token in Redis with 1-hour expiry
            redis.setex(List.of(token, "3600", userId), res -> {
                if (res.succeeded()) {
                    System.out.println("Login successful. JWT: " + token);
                } else {
                    System.err.println(" Failed to store token: " + res.cause().getMessage());
                }
            });

        }).onFailure(err -> {
            System.err.println(" Login error: " + err.getMessage());
        });
    }

    public void logout(String token) {
        redis.del(List.of(token), res -> {
            if (res.succeeded()) {
                System.out.println("Logged out and token invalidated.");
            } else {
                System.err.println("Logout failed: " + res.cause().getMessage());
            }
        });
    }
}
