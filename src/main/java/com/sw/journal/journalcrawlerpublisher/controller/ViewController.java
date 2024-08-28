package com.sw.journal.journalcrawlerpublisher.controller;

import com.sw.journal.journalcrawlerpublisher.service.ArticleRankService;
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
public class ViewController {
    private final StringRedisTemplate redisTemplate;
    private final ArticleRankService articleRankService;

    @GetMapping("/view-article")
    public String viewArticle(
            @RequestParam("transactionKey") String transactionKey,
            @RequestParam("userId") String userId,
            @RequestParam("articleId") Long articleId
    ) {
        // Redis에서 트랜잭션 키 확인
        Boolean isValidTransaction = redisTemplate.hasKey(transactionKey);

        if (Boolean.FALSE.equals(isValidTransaction)) {
            return "Invalid or expired transaction key.";
        }

        // Redis에서 조회수 증가 여부 확인 (이미 조회했는지 확인)
        String redisKey = userId + ":" + articleId;
        Boolean hasViewed = redisTemplate.hasKey(redisKey);

        // 조회 기록 로깅 (모든 조회 기록)
        logView(userId, articleId);

        if (Boolean.TRUE.equals(hasViewed)) {
            // 이미 조회한 경우: 조회수 증가 없이 로그만 남김
            return "View recorded but count not increased (already viewed within 24 hours).";
        } else {
            // 처음 조회하는 경우: 조회수 증가 및 Redis에 기록
            articleRankService.increaseArticleCount(articleId);
            redisTemplate.opsForValue().set(redisKey, "viewed", 24, TimeUnit.HOURS); // 24시간 TTL 설정
            return "View recorded and count increased.";
        }
    }

    private void logView(String userId, Long articleId) {
        // 여기에 로깅 로직을 구현합니다.
        // 예: 로그 파일에 기록하거나 데이터베이스에 저장할 수 있습니다.
        System.out.println("User " + userId + " viewed article " + articleId);
    }
}
