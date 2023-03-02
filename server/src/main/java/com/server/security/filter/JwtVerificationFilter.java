package com.server.security.filter;

import com.server.domain.member.entity.Member;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.security.JwtTokenizer;
import com.server.security.service.RedisService;
import com.server.security.utils.MemberAuthorityUtils;
import com.server.security.utils.MemberDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter { // jwt 검증 전용 필터, OncePerRequestFilter(리퀘스트 당 한번만 실행되는 필터)
    private final JwtTokenizer jwtTokenizer;
    private final MemberAuthorityUtils authorityUtils;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }
    // 조건에 부합하면 해당 필터의 동작을 수행하고, 부합하지 않으면 다음 필터로 건너뛰는 메서드
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }
    // token 인증
    private Map<String, Object> verifyJws(HttpServletRequest request) {
        // request header에 들어온 accesstoken 검증
        String jws = request.getHeader("Authorization").replace("Bearer ", "");

        if(StringUtils.hasText(redisService.getAccessToken(jws))) {
            throw new UnsupportedJwtException("로그아웃 된 토큰입니다.");
        }

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }
    // 인증된 정보를 security context에 저장
    private void setAuthenticationToContext(Map<String, Object> claims) {
        long memberId = Long.parseLong(String.valueOf(claims.get("memberId")));
        String email = (String) claims.get("email");
        List<String> roles = (List<String>) claims.get("roles");

        Member member = Member.builder()
                .memberId(memberId)
                .email(email)
                .roles(roles)
                .build();

        List<GrantedAuthority> authorities = authorityUtils.createAuthorities(roles);

        MemberDetails memberDetails = new MemberDetails(authorityUtils, member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, authorities);

        log.info("Set Authentication in security context : {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
