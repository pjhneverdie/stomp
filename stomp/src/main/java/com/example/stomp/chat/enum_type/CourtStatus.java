package com.example.stomp.chat.enum_type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CourtStatus {
    STEP_STAND_BY(""), // step which waits for both user's entrance.
    STEP_APPEALING(""), // step which explains the situation in each point of view to LLM.
    STEP_ARGUING(""), // step which argues right and wrong based on each appealing.
    STEP_JUDJED(""); // step where LLM makes the final decision.

    private final String value;
}
