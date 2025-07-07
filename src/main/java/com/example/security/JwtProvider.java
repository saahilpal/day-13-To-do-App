package com.example.security;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;

import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class JwtProvider {

    private final JWTAuth jwtAuth;

    public JwtProvider(String secret) {
        this.jwtAuth = JWTAuth.create(null, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setBuffer(secret)));
    }

    public String generateToken(String userId) {
        JsonObject claims = new JsonObject().put("userId", userId);
        JWTOptions options = new JWTOptions().setExpiresInMinutes(60);
        return jwtAuth.generateToken(claims, options);
    }

    public JWTAuth getJwtAuth() {
        return jwtAuth;
    }
}
