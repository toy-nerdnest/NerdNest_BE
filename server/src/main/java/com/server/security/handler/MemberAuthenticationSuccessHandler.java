package com.server.security.handler;

import com.server.domain.member.entity.Member;
import com.server.security.utils.MemberDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    // 로그인 성공했을 때, 수행해야 할 로직 구현
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        MemberDetails member = (MemberDetails) authentication.getPrincipal();
        String username = member.getEmail();

        log.info("# Authenticated Success!, username : {}", username);

    }
}
