package com.example.stomp.security.repository;

import java.util.function.Supplier;

import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RedisSessionRepository implements SecurityContextRepository {

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        // OAuth2LoginAuthenticationFilter에서 save하면 여기서 세션 만들고 redis에도 저장해야 함
        // 그럼 알아서 세션있네? 확인하고 요청에 쿠키 포함시킴
        throw new UnsupportedOperationException("Unimplemented method 'saveContext'");
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'containsContext'");
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> supplier = () -> readSecurityContextFromSession(request.getSession(false));

        return new DeferredSecurityContext() {
            @Override
            public SecurityContext get() {
                return supplier.get();
            }

            @Override
            public boolean isGenerated() {
                return false;
            }
        };
    }

    private SecurityContext readSecurityContextFromSession(HttpSession session) {
        throw new UnsupportedOperationException("Unimplemented method 'readSecurityContextFromSession'");
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

}
