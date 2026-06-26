package com.example.stomp.trial.domain.repository;

import com.example.stomp.trial.domain.Trial;
import com.example.stomp.trial.domain.TrialStage;

public interface TrialRepository {

    Trial save(Trial trial);

    long countUnTerminatedTrialByMemberId(Long memberId, TrialStage stage);

}
