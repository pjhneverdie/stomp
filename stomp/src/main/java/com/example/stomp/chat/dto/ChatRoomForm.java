package com.example.stomp.chat.dto;

import jakarta.validation.constraints.NotBlank;

public abstract class ChatRoomForm {

    private ChatRoomForm() {
    }

    public record Create(@NotBlank String name) {
    }

}
