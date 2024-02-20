package com.moim.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://api.moidot.co.kr")
                .allowedOrigins("https://api.moidot.co.kr/")
                .allowedOrigins("https://moidot.vercel.app")
                .allowedOrigins("https://moidot.vercel.app/")
                .allowedOrigins("https://www.moidot.co.kr")
                .allowedOrigins("https://www.moidot.co.kr/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
