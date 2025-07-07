package com.example.route;

import com.example.service.AuthService;
import com.example.service.PasswordResetService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class AuthRoutes {

    public static void mount(Router router, AuthService authService, PasswordResetService resetService) {
        router.post("/api/auth/register").handler(ctx -> handleRegister(ctx, authService));
        router.post("/api/auth/login").handler(ctx -> handleLogin(ctx, authService));
        router.post("/api/auth/logout").handler(ctx -> handleLogout(ctx, authService));
        router.post("/api/auth/reset/initiate").handler(ctx -> handleResetRequest(ctx, resetService));
        router.post("/api/auth/reset/complete").handler(ctx -> handleResetComplete(ctx, resetService));
    }

    private static void handleRegister(RoutingContext ctx, AuthService authService) {
        ctx.request().body().onSuccess(body -> {
            String email = body.toJsonObject().getString("email");
            String name = body.toJsonObject().getString("name");
            authService.register(email, name);
            ctx.response().setStatusCode(201).end("User registered.");
        });
    }

    private static void handleLogin(RoutingContext ctx, AuthService authService) {
        ctx.request().body().onSuccess(body -> {
            String email = body.toJsonObject().getString("email");
            String password = body.toJsonObject().getString("password");
            authService.login(email, password);
            ctx.response().setStatusCode(200).end("Logged in (check console for JWT)");
        });
    }

    private static void handleLogout(RoutingContext ctx, AuthService authService) {
        String token = ctx.request().getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(token.substring(7));
            ctx.response().end("Logged out");
        } else {
            ctx.response().setStatusCode(400).end("Missing token");
        }
    }

    private static void handleResetRequest(RoutingContext ctx, PasswordResetService resetService) {
        ctx.request().body().onSuccess(body -> {
            String email = body.toJsonObject().getString("email");
            resetService.initiatePasswordReset(email);
            ctx.response().end("Reset link sent.");
        });
    }

    private static void handleResetComplete(RoutingContext ctx, PasswordResetService resetService) {
        ctx.request().body().onSuccess(body -> {
            String email = body.toJsonObject().getString("email");
            String token = body.toJsonObject().getString("token");
            String newPassword = body.toJsonObject().getString("newPassword");
            resetService.completePasswordReset(email, token, newPassword);
            ctx.response().end("Password reset successful.");
        });
    }
}
