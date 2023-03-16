package com.server.security.controller;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
import com.server.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final MemberRepository memberRepository;

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

    @GetMapping("/oauth")
    public ResponseEntity oauthLogin(@RequestParam("email") String email,
                                     HttpServletResponse response) throws IOException {

        authService.oauthMember(response, email);

        return new ResponseEntity<>("Success OAuth2User login", HttpStatus.OK);
    }
}
