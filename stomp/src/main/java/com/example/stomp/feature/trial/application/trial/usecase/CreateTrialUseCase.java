package com.example.stomp.feature.trial.application.trial.usecase;

import org.springframework.stereotype.Service;

import com.example.stomp.feature.member.domain.Member;
import com.example.stomp.feature.member.service.MemberService;
import com.example.stomp.feature.trial.application.trial.dto.ChatExceptions;
import com.example.stomp.feature.trial.entity.Trial;
import com.example.stomp.feature.trial.entity.TrialStage;
import com.example.stomp.feature.trial.repository.TrialRepository;
import com.example.stomp.shared.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateTrialUseCase {
    private final MemberService memberService;
    private final TrialRepository trialRepository;

    public String create(Long memberId, String issueTitle, String nickname) {
        Member member = memberService.findById(memberId);

        if (trialRepository.countUnTerminatedTrialByMemberId(member.getId(), TrialStage.TERMINATED) > 0) {
            throw new AppException(ChatExceptions.ONGOING_CHAT_EXISTS);
        }

        return trialRepository.save(Trial.create(member, issueTitle, nickname)).getId().toString();
    }

}
