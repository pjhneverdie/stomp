package com.example.stomp.jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.config.JwtContants.TokenType;
import com.example.stomp.jwt.dto.requestscope.JwtContext;
import com.example.stomp.member.dto.JwtMemberDetails;
import com.example.stomp.shared.util.SecurityUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityContextFilter extends OncePerRequestFilter {
    private final JwtContext jwtContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        JwtContants.TokenType tokenType = TokenType
                .valueOf((String) request.getAttribute(JwtContants.TYPE_DISCRIMINATOR_KEY));

        switch (tokenType) {
            case ACCESS:
                Long id = Long.parseLong(jwtContext.getAccessTokenClaims().getSubject());
                String roles = (String) jwtContext.getAccessTokenClaims().get(JwtContants.ROLES_KEY);

                JwtMemberDetails jwtMemberDetails = new JwtMemberDetails(
                        id,
                        SecurityUtil.stringToAuthorities(roles));

                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(jwtMemberDetails, null,
                                jwtMemberDetails.getAuthorities()));
                break;

            case REFRESH:
                break;
        }

        filterChain.doFilter(request, response);
    }

}
