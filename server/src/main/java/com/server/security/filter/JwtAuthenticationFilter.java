package com.server.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.member.entity.Member;
import com.server.security.JwtTokenizer;
import com.server.security.dto.LoginDto;
import com.server.security.service.AuthService;
import com.server.security.service.RedisService;
import com.server.security.utils.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final RedisService redisService;
    private final AuthService authService;

    // login 요청 시, 인증을 시도하는 로직
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    // 인증에 성공할 경우 호출되는 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        MemberDetails member = (MemberDetails) authResult.getPrincipal();

        String accessToken = authService.delegateAccessToken(member);
        String refreshToken = authService.delegateRefreshToken(member);

        // todo 생성된 refreshToken redis에 저장해야 함.
        if(redisService.getRefreshToken(member.getEmail())==null) {
            redisService.saveRefreshToken(member.getEmail(), refreshToken, jwtTokenizer.getRefreshTokenExpirationMinutes());
            log.info("save Refresh Token in redis server!");
        }
        //token response body에 넣기
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("memberId", member.getMemberId());
        responseBody.put("accessToken", "Bearer " + accessToken);
        responseBody.put("refreshToken", refreshToken);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }


}
