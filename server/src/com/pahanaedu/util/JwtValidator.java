package com.pahanaedu.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtValidator {
    private static final String SECRET_KEY = "pahanaEdu9090";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    public static String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }
}
