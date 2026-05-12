package com.example.stomp.app.constant;

public final class SessionConstant {
    public static final String COOKIE_PATH = "/";
    public static final String COOKIE_NAME = "SOLOMON";

    public static final String SESSION_HKEY_PREFIX = "session:";
    public static final String SESSION_MEMBER_ID_FKEY = "memberId";
    public static final String SESSION_AUHTORITIES_FKEY = "authorities";
    public static final String SESSION_HTTP_SESSION_ID_FKEY = "httpSessionId";
    public static final String SESSION_WS_SESSION_ID_FKEY = "wsSessinId";

    public static final String SESSION_REVERSE_INDEX_KEY_PREFIX = "memberId";

    public static final String CLEAN_UP_WAITING_HKEY = "cleanings";

    public static final int SESSION_VALID_DAYS = 1;
}
