package com.sw.journal.journalcrawlerpublisher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories // Spring Data Redis 리포지토리를 활성화
// 리포지토리 인터페이스 정의와 사용 가능
public class RedisConfig {
    // application.properties에서 가져옴
    @Value("${spring.data.redis.host}") // Redis 서버 호스트
    private String host;

    @Value("${spring.data.redis.port}") // Redis 서버 포트
    private int port;

    @Bean // 기존 네이밍 빈이 관문이되서 삭제 불가
    public RedisConnectionFactory redisConnectionFactory() {
        // 메서드명이 Bean 이름이 됨
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(
                        // 원격 이용 및 Docker 사용을 염두에 두고 full args 생성자 사용
                        host, port)
        );
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory0() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration(
                host, port
        );
        redisConf.setDatabase(0);
        return new LettuceConnectionFactory(redisConf);
    }

    // ================ Redis 접속객체 생성 완료 ==================
    // 커맨드 구조를 미리 정의하는 Template 객체를 사용해야 실제 Redis 호출 가능


    @Bean // 빈을 없엘 순 없음
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory redisConnectionFactory // 없엘 수 없음
            // 메서드 시그니처를 반드시 따라야하는 메서드가 많음
    ) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public StringRedisTemplate redisTemplateDb0() {
        return new StringRedisTemplate(redisConnectionFactory0());
    }

    @Bean
    public RedisTemplate<String, String> redisObjTemplateDb0() {  // Redis 키는 String 값은 Object
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory0());

        // Long 타입 값을 직렬화하기 위한 직렬화기
        GenericToStringSerializer<Long> longSerializer = new GenericToStringSerializer<>(Long.class);

        // 객체 타입 값을 직렬화하기 위한 직렬화기
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // 조건에 따라 serializer 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
