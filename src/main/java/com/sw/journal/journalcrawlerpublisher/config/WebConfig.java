package com.sw.journal.journalcrawlerpublisher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://itscribe.site", "http://www.itscribe.site") // React 앱의 URL
                        .allowedMethods("*")
                        .allowedHeaders("*") // 모든 헤더를 허용
                        .allowCredentials(true); // 쿠키 및 인증 정보를 허용
            }
        };
    }
}