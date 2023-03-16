package com.server.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.security.JwtTokenizer;
import com.server.security.service.RedisService;
import com.server.security.utils.MemberAuthorityUtils;
import com.server.security.utils.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler { // oauth 인증 후, 프론트에게 jwt 전달 역할

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var oAuth2User = (OAuth2User)authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");

        log.info("google, oauthUser email : {}", email);
        String path = "oauth/google/login";

        if(email == null) {
            Map<Object, Object> kakaoAccount = (Map<Object, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
            path = "oauth/kakao/login";
            log.info("Kakao, oauthUser email : {}", email);
        }

        redirect(request, response, email, path);
        log.info("OAuth2 Login Success");
    }


    private void redirect(HttpServletRequest request, HttpServletResponse response, String email, String path) throws IOException {
        String uri = createURI(email, path).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI(String email, String path) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("email", email);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
//                .host("nerdnest.s3-website.ap-northeast-2.amazonaws.com")
                .host("localhost")
                .port(8080)
                .path(path)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

}
