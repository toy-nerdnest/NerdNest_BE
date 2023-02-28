package com.server.security.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberAuthorityUtils {
    private final List<String> USER_ROLE = List.of("ROLE_USER");

    public List<String> createRole() {
        return this.USER_ROLE;
    }

    public List<GrantedAuthority> createAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
