package com.example.stomp.jwt.config;

public abstract class JwtContants {

    public static final String ROLES_KEY = "roles";
    public static final String TYPE_DISCRIMINATOR_KEY = "token_type";

    public static final String REFRESH_TOKEN_PREFIX = "rt:";
    public static final String BLACKLIST_PREFIX = "black:";

    public static enum TokenType {
        ACCESS,
        REFRESH;
    }

    public static enum BlackReason {
        LOGOUT,
        REISSUE
    }

    private JwtContants() {
    }

}