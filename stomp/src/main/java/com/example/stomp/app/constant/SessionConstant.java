package com.example.stomp.app.constant;

public class SessionConstant {
    public static final String COOKIE_PATH = "/";
    public static final String COOKIE_NAME = "SOLOMON";

    public static final String SESSION_KEY_PREFIX = "session:"; // session key is like "session:{sessionId}"
    public static final String SESSION_MEMBER_ID_KEY = "memberId";
    public static final String SESSION_SESSION_ID_KEY = "sessionId";
    public static final String SESSION_MEMBER_CODE_KEY = "memberCode";
    public static final String SESSION_AUHTORITIES_KEY = "authorities";
    public static final String SESSION_ROOM_ID_KEY = "roomId";

    // we have to able to find the member's session even when we have only memberId
    // this is a index key for making above happen
    public static final String MEMBER_SESSION_INDEX_KEY_PREFIX = "member:session:";
}
