package com.example.stomp.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public abstract class ChatRoomForm {

    private ChatRoomForm() {
    }

    public record Create(@NotBlank String issueTitle) {
    }

    public record Join(@NotNull Long memberId, @NotBlank String roomUuid,
            @NotBlank @Size(min = 2, max = 16) String nickname) {
    }

}
