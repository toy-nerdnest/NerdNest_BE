package com.server.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    // refreshToken 저장
    public void saveRefreshToken(String email, String refreshToken, long expiration) {
        // redis-> key-value 타입 key: email, value : token, duration : 캐시에 데이터가 남아있는 시간
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(email, refreshToken, Duration.ofMinutes(expiration));
        log.info("refresh token 만료 시간 : {} 분", Duration.ofMinutes(expiration));
    }

    //logout - accessToken 저장
    public void saveAccessToken(String accessToken, long expiration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        log.info("save logout token in redis server!");
    }

    // refreshToken 가져오기
    public String getRefreshToken(String email) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String refreshToken = values.get(email);
        return refreshToken;
    }

    // accessToken 가져오기
    public String getAccessToken(String accessToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String logoutToken = values.get(accessToken);
        return logoutToken;
    }

    // refreshToken 삭제
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
