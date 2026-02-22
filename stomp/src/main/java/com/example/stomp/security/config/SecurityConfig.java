package com.example.stomp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.stomp.member.service.SimpleOidcUserService;
import com.example.stomp.security.filter.LoginFilter;
import com.example.stomp.security.filter.LogoutFilter;
import com.example.stomp.security.filter.ReissueFilter;
import com.example.stomp.security.filter.SecurityContextFilter;
import com.example.stomp.security.handler.OicdLoginSuccessHandler;
import com.example.stomp.security.handler.SecurityExceptionHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final SimpleOidcUserService simpleOidcUserService;
        private final OicdLoginSuccessHandler oicdLoginSuccessHandler;
        private final SecurityExceptionHandler securityExceptionHandler;

        private final LoginFilter loginFilter;
        private final ReissueFilter reissueFilter;
        private final LogoutFilter logoutFilter;
        private final SecurityContextFilter securityContextFilter;

        public static final String REISSUE_URL = "/auth/reissue";
        public static final String LOGOUT_URL = "/auth/logout";

        public static final String[] UNAUTHENTICATABLE_URL = {
                        "/",
                        "/favicon.ico",
                        REISSUE_URL
        };

        public static final String[] LOGIN_FILTER_WHITE_LIST = {
                        "/",
                        "/favicon.ico",
                        REISSUE_URL,
                        LOGOUT_URL
        };

        @Bean
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
                http.csrf(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable);

                http.oauth2Login((config) -> config
                                .userInfoEndpoint((endpoint) -> endpoint.oidcUserService(simpleOidcUserService))
                                .successHandler(oicdLoginSuccessHandler)
                                .failureHandler(securityExceptionHandler))
                                .exceptionHandling(
                                                (config) -> config
                                                                .authenticationEntryPoint(securityExceptionHandler)
                                                                .accessDeniedHandler(securityExceptionHandler));

                http.authorizeHttpRequests((authorize) -> authorize
                                .requestMatchers(UNAUTHENTICATABLE_URL)
                                .permitAll()
                                .anyRequest().authenticated());

                http.addFilterBefore(reissueFilter, UsernamePasswordAuthenticationFilter.class);
                http.addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter.class);
                http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
                http.addFilterAfter(securityContextFilter, LoginFilter.class);

                return http.build();
        }

}
