package com.example.stomp.trial.event;

import java.util.UUID;

public record TrialCreatedEvent(
        UUID trialId,
        Long memberId,
        String issueTitle,
        String nickname) {
}