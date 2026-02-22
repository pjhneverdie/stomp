package com.example.stomp.jwt.dto.requestscope;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class JwtContext {

    private Claims accessTokenClaims;
    private Claims refreshTokenClaims;

}
