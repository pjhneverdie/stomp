package com.example.stomp.shared.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public abstract class SecurityUtil {

    private static final String AUTHORITY_DELIMITER = ",";

    public static String authoritiesToString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(AUTHORITY_DELIMITER));
    }

    public static List<GrantedAuthority> stringToAuthorities(String authoritiesString) {
        return Arrays.stream(authoritiesString.split(AUTHORITY_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
