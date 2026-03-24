package com.example.stomp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.member.service.SimpleOidcUserService;
import com.example.stomp.security.handler.OicdLoginSuccessHandler;
import com.example.stomp.security.handler.SecurityExceptionHandler;
import com.example.stomp.security.handler.SessionLogoutSuccessHandler;
import com.example.stomp.security.repository.RedisOAuth2AuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final SimpleOidcUserService simpleOidcUserService;
        private final OicdLoginSuccessHandler oicdLoginSuccessHandler;
        private final SecurityExceptionHandler securityExceptionHandler;
        private final SessionLogoutSuccessHandler sessionLogoutSuccessHandler;
        private final RedisOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

        private static final String LOGOUT_PATH = "/logout";

        @Profile({ "local", "test" })
        public WebSecurityCustomizer webSecurityCustomizerDebug() {
                return (web) -> web.debug(true);
        }

        @Bean
        @Profile("prod")
        public WebSecurityCustomizer webSecurityCustomizerProd() {
                return (web) -> web.debug(false);
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/").permitAll()
                                                .anyRequest().authenticated());

                http
                                .httpBasic(AbstractHttpConfigurer::disable) // we are using https session login
                                .formLogin(AbstractHttpConfigurer::disable); // we are using OIDC
                http
                                .requestCache(requestCache -> requestCache.disable()); // we provide API only

                http.sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .securityContext(context -> context
                                                .requireExplicitSave(false)); // auto save

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

                http.logout(logout -> logout
                                .logoutUrl(LOGOUT_PATH)
                                .deleteCookies(SessionConstant.COOKIE_NAME)
                                .invalidateHttpSession(true)
                                .logoutSuccessHandler(sessionLogoutSuccessHandler));

                return http.build();
        }

}
