package com.server.security.filter;

import com.google.gson.Gson;
import com.server.exception.ExceptionCode;
import com.server.response.ErrorResponse;
import com.server.security.JwtTokenizer;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
public class JwtReissueFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // reissue 요청이 아니면 해당 필터는 건뛰
        String method = request.getMethod();
        String path = request.getRequestURI();
        String refreshToken = request.getHeader("Refresh");

        return !path.equals("/reissue") || !method.equals("POST") || !StringUtils.hasText(refreshToken);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 만료 시간 검증 -> access, refresh
        try{
            // 토큰 검증
            validTokenExpiration(request);
        } catch (ExpiredJwtException ee) {
            // senderror
            request.setAttribute("exception", ee);
            sendErrorResponse(response);
            return;
        } catch (MalformedJwtException me) {
            // senderror
            request.setAttribute("exception", me);
            sendErrorResponse(response);
            return;
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    private void validTokenExpiration(HttpServletRequest request) {
        // 엑세스 토큰과 리프레쉬 토큰 검증
        // 액세스 토큰은 당근빠다 만료된 상태일 것.. 예외를 던질 필요업슴
        String accessToken = request.getHeader("Authorization").replace("Bearer ", "");
        String refreshToken = request.getHeader("Refresh");

        if(!tokenExpiration(refreshToken)) {
            log.info("RefreshToken 만료");
            throw new ExpiredJwtException(null, null, "Refresh");
        }

    }
    private boolean tokenExpiration(String jwt) {
        long expiration = jwtTokenizer.getExpiration(jwt);

        if(expiration <= 0) {
            log.info("토큰 시간 만료");
            return false;
        }

        return true;
    }
    private HttpServletResponse sendErrorResponse(HttpServletResponse response) throws IOException {
            Gson gson = new Gson();
            ErrorResponse errorResponse = ErrorResponse.of(ExceptionCode.REFRESH_TOKEN_EXPIRATION);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));

            return response;
    }
}
