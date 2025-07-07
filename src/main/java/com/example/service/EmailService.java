package com.example.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

public class EmailService {

    private final MailClient mailClient;
    private final String fromAddress;

    public EmailService(Vertx vertx, JsonObject config) {
        this.fromAddress = config.getString("smtp_user");

        MailConfig mailConfig = new MailConfig()
                .setHostname(config.getString("smtp_host"))
                .setPort(config.getInteger("smtp_port"))
                .setStarttls(StartTLSOptions.REQUIRED)
                .setUsername(config.getString("smtp_user"))
                .setPassword(config.getString("smtp_password"));

        this.mailClient = MailClient.create(vertx, mailConfig);
    }

    public void sendEmail(String to, String subject, String content) {
        MailMessage message = new MailMessage()
                .setFrom(fromAddress)
                .setTo(to)
                .setSubject(subject)
                .setText(content);

        mailClient.sendMail(message, result -> {
            if (result.succeeded()) {
                System.out.println("✅ Email sent to " + to);
            } else {
                System.err.println("❌ Failed to send email: " + result.cause().getMessage());
            }
        });
    }

    public void sendRegistrationPassword(String to, String password) {
        String subject = "Welcome to To-Do App – Your Password";
        String content = "Your account has been created.\nYour password is: " + password + "\nPlease log in and change it.";
        sendEmail(to, subject, content);
    }

    public void sendPasswordResetLink(String to, String resetToken) {
        String subject = "To-Do App – Password Reset";
        String content = "You requested a password reset.\nUse this token to reset your password:\n\n" +
                resetToken + "\n\nThis token expires in 15 minutes.";
        sendEmail(to, subject, content);
    }
}
