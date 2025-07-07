package com.example.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verify(String password, String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }
}
