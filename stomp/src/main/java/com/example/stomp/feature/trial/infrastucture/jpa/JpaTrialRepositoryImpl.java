package com.example.stomp.feature.trial.infrastucture.jpa;

import org.springframework.stereotype.Repository;

import com.example.stomp.feature.trial.entity.Trial;
import com.example.stomp.feature.trial.entity.TrialStage;
import com.example.stomp.feature.trial.repository.TrialRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaTrialRepositoryImpl implements TrialRepository {

    private final JpaTrialRepository repository;

    @Override
    public Trial save(Trial trial) {
        return repository.save(trial);
    }

    @Override
    public long countUnTerminatedTrialByMemberId(Long memberId, TrialStage stage) {
        return repository.countUnTerminatedTrialByMemberId(memberId, stage);
    }

}
