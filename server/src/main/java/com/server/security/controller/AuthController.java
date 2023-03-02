package com.server.security.controller;

import com.server.domain.member.entity.Member;
import com.server.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // token 재발급
    @PostMapping("/reissue")
    public ResponseEntity reissueToken(HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {

        authService.reissueToken(request, response);

        return new ResponseEntity<>("토큰 재발급 성공" ,HttpStatus.OK);
    }
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request,
                                 HttpServletResponse response) {

        authService.logoutMember(request, response);

        return new ResponseEntity<>("로그아웃 완료",HttpStatus.OK);
    }
}
