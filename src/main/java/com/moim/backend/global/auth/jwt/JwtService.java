package com.moim.backend.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService implements InitializingBean {

    private final JwtProperties jwtProperties;
    private long tokenValidityInMillySeconds;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        tokenValidityInMillySeconds = jwtProperties.getTokenValidityInSeconds() * 1000;
    }

    public String createToken(String mail) {
        Date validity = new Date(System.currentTimeMillis() + tokenValidityInMillySeconds);

        return Jwts.builder()
                .setSubject(mail)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public boolean isValidated(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public String getUserEmail(String token) {
        Claims body = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();

        return body.getSubject();
    }

    public String getToken(HttpServletRequest request) {
        return getToken(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    public String getToken(NativeWebRequest request) {
        return getToken(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    private String getToken(String authorizationValue) {
        String[] splitAuthorization = authorizationValue.split(" ");
        if (splitAuthorization.length > 1) {
            return splitAuthorization[1];
        } else {
            return splitAuthorization[0];
        }
    }
}
