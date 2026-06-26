package com.example.stomp.trial.usecase;

import org.springframework.stereotype.Service;

import com.example.stomp.application.dto.AppException;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.service.MemberService;
import com.example.stomp.trial.domain.Trial;
import com.example.stomp.trial.domain.TrialStage;
import com.example.stomp.trial.domain.repository.TrialRepository;
import com.example.stomp.trial.dto.ChatExceptions;

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
