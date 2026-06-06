package com.example.stomp.app.constant;

public final class SessionKeys {

    private SessionKeys() {
    }

    public static final String COOKIE_PATH = "/";
    public static final String COOKIE_NAME = "SOLOMON";

    public static String session(String sessionId) {
        return "session:" + sessionId;
    }

    public static final String HFKEY_MEMBER_ID = "member_id";
    public static final String HFKEY_AUTHORITIES = "authorities";
    public static final String HFKEY_HTTP_SESSION_ID = "http_session_id";
    public static final String HFKEY_WS_SESSION_ID = "ws_session_id";

    // Reverse index key for member id -> session mapping.
    public static String reverseIndex(String memberId) {
        return "member_id:" + memberId;
    }

}
