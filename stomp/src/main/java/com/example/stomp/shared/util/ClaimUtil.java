package com.example.stomp.shared.util;

import com.example.stomp.jwt.config.JwtContants;

import io.jsonwebtoken.Claims;

public abstract class ClaimUtil {

    public static Long getMemberId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public static String getRoles(Claims claims) {
        return (String) claims.get(JwtContants.ROLES_KEY);
    }

}
