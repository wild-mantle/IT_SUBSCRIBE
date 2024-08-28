package com.sw.journal.journalcrawlerpublisher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {
    private final StringRedisTemplate redisTemplate;

    @GetMapping("/transaction-key")
    public String getTransactionKey(
            @RequestParam("userId") String userId,
            @RequestParam("articleId") Long articleId
    ) {
        // Redis 키 생성
        String redisKey = userId + ":" + articleId;

        // 따닥 방지용 키 생성 (1초 TTL)
        String lockKey = "lock:" + redisKey;
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 1, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isLocked)) {
            // 따닥 발생: 동일한 요청이 1초 이내에 다시 발생한 경우
            return "Request ignored due to rapid consecutive requests (anti-doubled click).";
        }

        // 트랜잭션 키 생성 (여기서는 단순히 redisKey 자체를 트랜잭션 키로 사용)
        String transactionKey = redisKey + "-" + System.currentTimeMillis();

        // Redis에 트랜잭션 키와 유효 기간 설정 (여기서는 1분 TTL)
        redisTemplate.opsForValue().set(transactionKey, "active", 1, TimeUnit.MINUTES);

        return transactionKey;
    }
}
