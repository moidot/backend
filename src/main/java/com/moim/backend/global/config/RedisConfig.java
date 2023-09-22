package com.moim.backend.global.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.port}")
    public int port;
    @Value("${spring.data.redis.host}")
    public String host;
    @Value("${spring.data.redis.ssl.enabled}")
    private boolean sslEnabled;
    private final ResourceLoader resourceLoader;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

//    @Bean
//    LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(host);
//        redisStandaloneConfiguration.setPort(port);
//
//        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder =
//                LettuceClientConfiguration.builder();
//
//        if (sslEnabled) {
//            SslOptions sslOptions = null;
//            try {
//                sslOptions = SslOptions.builder()
//                        .trustManager(resourceLoader.getResource("classpath:secret/redis.pem").getFile())
//                        .build();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            ClientOptions clientOptions = ClientOptions
//                    .builder()
//                    .sslOptions(sslOptions)
//                    .protocolVersion(ProtocolVersion.RESP3)
//                    .build();
//
//            lettuceClientConfigurationBuilder
//                    .clientOptions(clientOptions)
//                    .useSsl();
//        }
//
//        LettuceClientConfiguration lettuceClientConfiguration = lettuceClientConfigurationBuilder.build();
//
//        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
//    }

    @Bean
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
