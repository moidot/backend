package com.moim.backend.global.auth.jwt;

import com.moim.backend.global.common.RedisKeyPrefix;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import com.moim.backend.global.util.RedisDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static com.moim.backend.global.common.RedisKeyPrefix.*;
import static com.moim.backend.global.common.Result.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService implements InitializingBean {

    private final RedisDao redisDao;
    private final JwtProperties jwtProperties;
    private long tokenValidityInMillySeconds;
    private long refreshTokenValidityInMillySeconds;
    private Key key;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        tokenValidityInMillySeconds = jwtProperties.getTokenValidityInMinutes() * 60 * 1000;
        refreshTokenValidityInMillySeconds = jwtProperties.getRefreshTokenValidityInMinutes() * 60 * 1000;
        valueOperations = redisTemplate.opsForValue();
    }

    public String createAccessToken(String mail) {
        Date validity = new Date(System.currentTimeMillis() + tokenValidityInMillySeconds);

        return Jwts.builder()
                .setSubject(mail)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public void validateToken(String token) {
        if (redisDao.getValues(token) != null) {
            throw new CustomException(IS_TOKEN_LOGOUT);
        }
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(IS_TOKEN_LOGOUT);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
        }
    }

    public String getUserEmail(String token) {
        Claims body = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();

        return body.getSubject();
    }

    public String createRefreshToken(String mail) {
        Date validity = new Date(System.currentTimeMillis() + refreshTokenValidityInMillySeconds);
        String refreshToken = Jwts.builder()
                .setSubject(mail)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        String redisKey = REFRESH_TOKEN.getPrefix() + mail;
        valueOperations.set(redisKey, refreshToken);

        return refreshToken;
    }

    public String reissueAccessToken(String refreshToken) {
        validateToken(refreshToken);
        String mail = getUserEmail(refreshToken);
        String redisKey = REFRESH_TOKEN.getPrefix() + mail;
        if (refreshToken.equals(valueOperations.get(redisKey))) {
            return createAccessToken(mail);
        }
        return null;
    }

    public String getToken(NativeWebRequest request) {
        return getToken(Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)));
    }

    public String getToken(Optional<String> authorization) {
        String[] splitAuthorization = authorization
                .orElseThrow(() -> new NullPointerException("header에 authorization 값이 없습니다."))
                .split(" ");

        return (splitAuthorization.length > 1) ? splitAuthorization[1] : splitAuthorization[0];
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        // 현재 시간
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
