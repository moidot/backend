package com.moim.backend.global.auth.jwt;

import com.moim.backend.TestQueryDSLConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@EnableConfigurationProperties({JwtProperties.class})
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;

    @DisplayName("토큰 생성 테스트")
    @Test
    protected void createTokenTest() {
        // given
        String mail = "test@gmail.com";

        // when
        String token = jwtService.createToken(mail);
        String tokenPattern = ".+\\..+\\..+";

        // then
        assertTrue("토큰 생성", token.matches(tokenPattern));
    }

    @DisplayName("토큰 생성 테스트")
    @Test
    protected void createUnlimitTokenTest() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String accessToken = Jwts.builder()
                .setSubject("mingyung@gmail.com")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        System.out.println("accessToken = " + accessToken);
    }

    @DisplayName("token 값이 없을 때 예외 발생 테스트")
    @Test
    protected void validNullTokenTest() {
        // when // then
        assertThrows(
                NullPointerException.class,
                () -> jwtService.getToken(Optional.empty())
        );
    }

    @DisplayName("서명이 잘못되었을 때")
    @Test
    protected void invalidSignatureTest() {
        // given
        String mail = "test@gmail.com";
        String token = jwtService.createToken(mail);

        // when // then
        assertThrows(
                SignatureException.class,
                () -> jwtService.isValidated(token.substring(0, token.length() - 2))
        );
    }

    @DisplayName("헤더가 잘못되었을 때")
    @Test
    protected void invalidHeaderTest() {
        // given
        String mail = "test@gmail.com";
        String token = jwtService.createToken(mail);

        // when // then
        assertThrows(
                MalformedJwtException.class,
                () -> jwtService.isValidated(token.substring(1))
        );
    }

}

