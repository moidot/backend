package com.moim.backend.global.common;

import com.moim.backend.global.util.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    public static final String LOGOUT = "logout";
    private final RedisDao redisDao;

    public Void logoutFromRedis(String email, String accessToken, Long accessTokenExpiration) {
        redisDao.deleteValues(email);
        redisDao.setValues(accessToken, LOGOUT, Duration.ofMillis(accessTokenExpiration));
        return null;
    }
}
