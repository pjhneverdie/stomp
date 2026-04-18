package com.example.stomp.app.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static String authoritiesToString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public static List<SimpleGrantedAuthority> stringToAuthorities(String stringAuthorities) {
        return Arrays.stream(stringAuthorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public static RedisHttpSessionMemberPrincipal getPrincipal() {
        return (RedisHttpSessionMemberPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
