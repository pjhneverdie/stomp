package com.example.stomp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.member.service.OidcMemberService;
import com.example.stomp.security.handler.OicdLoginSuccessHandler;
import com.example.stomp.security.handler.RedisHttpSessionLogoutHandler;
import com.example.stomp.security.handler.SecurityExceptionHandler;
import com.example.stomp.security.handler.RedisHttpSessionLogoutSuccessHandler;
import com.example.stomp.security.repository.RedisHttpSessionContextRepository;
import com.example.stomp.security.repository.RedisHttpSessionAuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final OidcMemberService simpleOidcUserService;
        private final OicdLoginSuccessHandler oicdLoginSuccessHandler;
        private final SecurityExceptionHandler securityExceptionHandler;
        private final RedisHttpSessionLogoutHandler redisHttpSessionLogoutHandler;
        private final RedisHttpSessionLogoutSuccessHandler sessionLogoutSuccessHandler;
        private final RedisHttpSessionContextRepository redisHttpSessionContextRepository;
        private final RedisHttpSessionAuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

        private static final String LOGOUT_PATH = "/logout";

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(Customizer.withDefaults());

                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/favicon.ico", "/error").permitAll()
                                                .anyRequest().authenticated());

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .rememberMe(AbstractHttpConfigurer::disable)
                                .requestCache(requestCache -> requestCache.disable());

                http.oauth2Login((config) -> config
                                .userInfoEndpoint((endpoint) -> endpoint.oidcUserService(simpleOidcUserService))
                                .authorizationEndpoint(authorization -> authorization
                                                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository))
                                .successHandler(oicdLoginSuccessHandler)
                                .failureHandler(securityExceptionHandler))
                                .exceptionHandling(
                                                (config) -> config
                                                                .authenticationEntryPoint(securityExceptionHandler)
                                                                .accessDeniedHandler(securityExceptionHandler));

                http.securityContext(context -> context
                                .securityContextRepository(redisHttpSessionContextRepository));

                http.logout(logout -> logout
                                .logoutUrl(LOGOUT_PATH)
                                .addLogoutHandler(redisHttpSessionLogoutHandler)
                                .addLogoutHandler(new CookieClearingLogoutHandler(SessionConstant.COOKIE_NAME))
                                .logoutSuccessHandler(sessionLogoutSuccessHandler));

                return http.build();
        }

}
