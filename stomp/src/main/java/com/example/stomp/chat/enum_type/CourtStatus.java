package com.example.stomp.chat.enum_type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CourtStatus {
    STEP_STAND_BY(""), // 이해관계자들이 다 재판에 입장할 때까지 기다리는 상태
    STEP_APPEALING(""), // LLM에게 각자 입장에서 상황을 설명 중인 상태
    STEP_ARGUING(""), // 서로 잘잘못 따지고 있는 상태
    STEP_STOPPED(""), // 탈주로 재판이 멈춘 상태
    STEP_JUDJED(""); // 재판이 끝난 상태

    private final String status;
}
