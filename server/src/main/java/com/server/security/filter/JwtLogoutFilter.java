package com.server.security.filter;

import com.google.gson.Gson;
import com.server.exception.ExceptionCode;
import com.server.response.ErrorResponse;
import com.server.security.JwtTokenizer;
import com.server.security.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtLogoutFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final RedisService redisService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String accessToken = request.getHeader("Authorization");

        return !path.equals("/logout") || !method.equals("POST") || !StringUtils.hasText(accessToken) || !accessToken.startsWith("Bearer ");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // accessToken 만료 확인
        try {
            // 만료 시간 검증
            validTokenExpiration(request);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
            sendErrorResponse(response);
            return;
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request, response);
    }

    private void validTokenExpiration(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").replaceAll("Bearer ", "");

        long expiration = jwtTokenizer.getExpiration(accessToken);

        if(expiration <= 0) {
            log.info("Access token 만료");
            throw new ExpiredJwtException(null, null, "Authorization");
        }
    }

    private HttpServletResponse sendErrorResponse(HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        ErrorResponse errorResponse = ErrorResponse.of(ExceptionCode.ACCESS_TOKEN_EXPIRATION);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));

        return response;
    }
}
