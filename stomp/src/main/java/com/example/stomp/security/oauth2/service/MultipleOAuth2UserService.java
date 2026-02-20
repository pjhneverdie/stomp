package com.example.stomp.security.oauth2.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.github.javafaker.Faker;

public class MultipleOAuth2UserService extends DefaultOAuth2UserService {

    private final Faker faker = new Faker();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String email;
        String nickname = faker.funnyName().name();

        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "kakao":
                // 이메일만 추출
                break;

            default:
                break;
        }

        // db 조회
        // 유저 있음 Principal 만들어서 반환
        // 유저 없음 회원가입 닉네임은 랜덤닉

        return super.loadUser(userRequest);
    }

}
