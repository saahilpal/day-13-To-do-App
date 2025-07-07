package com.example.service;

import com.example.security.PasswordHasher;
import com.example.util.TokenUtils;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PasswordResetService {

    private final MongoCollection<Document> users;
    private final EmailService emailService;

    public PasswordResetService(com.mongodb.client.MongoClient mongoClient, EmailService emailService) {
        this.users = mongoClient.getDatabase("Todo").getCollection("data");
        this.emailService = emailService;
    }

    // 1. Generate token and email it
    public void initiatePasswordReset(String email) {
        Document user = users.find(Filters.eq("email", email)).first();
        if (user == null) {
            System.out.println("No user found with email.");
            return;
        }

        String token = TokenUtils.generateToken();
        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);

        users.updateOne(
                Filters.eq("email", email),
                Updates.combine(
                        Updates.set("resetToken", token),
                        Updates.set("resetTokenExpiry", expiresAt.toString())
                )
        );

        emailService.sendPasswordResetLink(email, token);
        System.out.println(" Reset token sent to email.");
    }

    // 2. Validate token and reset password
    public void completePasswordReset(String email, String token, String newPassword) {
        Document user = users.find(Filters.eq("email", email)).first();
        if (user == null || !token.equals(user.getString("resetToken"))) {
            System.out.println("Invalid token.");
            return;
        }

        Instant expiry = Instant.parse(user.getString("resetTokenExpiry"));
        if (Instant.now().isAfter(expiry)) {
            System.out.println("Token expired.");
            return;
        }

        String hashed = PasswordHasher.hash(newPassword);

        users.updateOne(
                Filters.eq("email", email),
                Updates.combine(
                        Updates.set("hashedPassword", hashed),
                        Updates.unset("resetToken"),
                        Updates.unset("resetTokenExpiry")
                )
        );

        System.out.println("assword reset successful.");
    }
}
