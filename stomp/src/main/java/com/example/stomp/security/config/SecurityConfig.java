package com.example.stomp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.example.stomp.member.service.SimpleOidcUserService;
import com.example.stomp.security.entrypoint.UnauthenticatedEntryPoint;
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
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico")
                .permitAll()
                .anyRequest().authenticated());

        return http.build();
    }

    
}
