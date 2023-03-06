package com.server.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.member.entity.Member;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.security.JwtTokenizer;
import com.server.security.utils.MemberAuthorityUtils;
import com.server.security.utils.MemberDetails;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RedisService redisService;
    private final JwtTokenizer jwtTokenizer;
    private final MemberDetailsService memberDetailsService;
    private final RedisTemplate redisTemplate;

    // accessToken 재발급
    public void reissueToken(HttpServletRequest request,HttpServletResponse response) throws IOException {

        String refreshToken = request.getHeader("Refresh");

        if(!jwtTokenizer.validateToken(refreshToken)) {
            throw new BusinessLogicException(ExceptionCode.REFRESH_TOKEN_EXPIRATION);
        }
        // refresh Token으로 redis에 저장된 email 값 가져오기
        String email = getEmail(refreshToken);

        // 새로운 access token을 생성해 response로 보내주기
        MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername(email);
        String accessToken = delegateAccessToken(memberDetails);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("memberId", memberDetails.getMemberId());
        body.put("accessToken", "Bearer " + accessToken);
        body.put("refreshToken", refreshToken);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    public void logoutMember(HttpServletRequest request, HttpServletResponse response) {
        // access token 검증
        String accessToken = request.getHeader("Authorization").replaceAll("Bearer ", "");

        if(!jwtTokenizer.validateToken(accessToken)) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_EXPIRATION);
        }

        String email = getEmail(accessToken);

        String refreshToken = redisService.getRefreshToken(email);

        if(!refreshToken.isEmpty()) {
            redisService.deleteRefreshToken(email);
        }
        // access token 유효시간을 가져와서 redis 서버에 logout된 token으로 저장
        long expiration = jwtTokenizer.getExpiration(accessToken);
        // key:token, value : logout
        redisService.saveAccessToken(accessToken, expiration);


    }

    // accessToken 생성 로직
    public String delegateAccessToken(MemberDetails member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getMemberId());
        claims.put("email",member.getEmail());
        claims.put("nickname",member.getNickName());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    // refreshToken 생성 로직
    public String delegateRefreshToken(MemberDetails member) {
        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refreshToken;
    }

    private String getEmail(String token){
        Claims claims = jwtTokenizer
                .getClaims(token, jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()))
                .getBody();

        String email = claims.getSubject();
        return email;
    }
}
