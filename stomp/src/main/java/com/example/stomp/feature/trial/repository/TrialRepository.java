package com.example.stomp.feature.trial.repository;

import com.example.stomp.feature.trial.entity.Trial;
import com.example.stomp.feature.trial.entity.TrialStage;

public interface TrialRepository {

    Trial save(Trial trial);

    long countUnTerminatedTrialByMemberId(Long memberId, TrialStage stage);

}
