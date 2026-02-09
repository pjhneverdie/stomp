package com.example.stomp.chat.enum_type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StakeholderStatus {
    STAND_BY(""),
    PREPARED(""),
    APPEALED(""),
    ARGUED("");

    private final String value;
}
