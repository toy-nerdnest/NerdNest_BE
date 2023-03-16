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
    private final JwtTokenizer jwtTokenizer;
    private final MemberAuthorityUtils authorityUtils;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var oAuth2User = (OAuth2User)authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");

        log.info("google, oauthUser email : {}", email);

        if(email == null) {
            Map<Object, Object> kakaoAccount = (Map<Object, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
            log.info("Kakao, oauthUser email : {}", email);
        }

        List<String> authorities = authorityUtils.createRole();

        redirect(request, response, email, authorities);
        log.info("OAuth2 Login Success");
    }


    private void redirect(HttpServletRequest request, HttpServletResponse response, String email, List<String> authorities) throws IOException {
//        String accessToken = delegateAccessToken(email, authorities);
//        String refreshToken = delegateRefreshToken(email);

        String uri = createURI(email).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

//    private String delegateAccessToken(String email, List<String> authorities) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("email", email);
//        claims.put("roles", authorities);
//
//        String subject = email;
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
//        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
//
//        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);
//        return accessToken;
//    }
//
//    private String delegateRefreshToken(String email) {
//        String subject = email;
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
//        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
//
//        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);
//
//        if(redisService.getRefreshToken(email)!=null) {
//            redisService.deleteRefreshToken(email);
//        }
//
//        redisService.saveRefreshToken(email, refreshToken, jwtTokenizer.getRefreshTokenExpirationMinutes());
//        log.info("save Refresh Token in redis server!");
//
//        return refreshToken;
//    }

    private URI createURI(String email) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("email", email);
//        queryParams.add("accessToken", "Bearer "+ accessToken);
//        queryParams.add("refreshToken", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("nerdnest.s3-website.ap-northeast-2.amazonaws.com")
//                .port(3000)
                .path("oauth/kakao/login")
                .queryParams(queryParams)
                .build()
                .toUri();
    }

}
