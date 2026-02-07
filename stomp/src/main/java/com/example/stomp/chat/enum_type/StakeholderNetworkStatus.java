package com.example.stomp.chat.enum_type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StakeholderNetworkStatus {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE");

    private final String value;
}
