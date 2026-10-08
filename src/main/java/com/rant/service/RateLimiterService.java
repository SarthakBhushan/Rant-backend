package com.rant.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    // Cache for post creation: 1 request per 24 hours
    private final Cache<String, Bucket> postBuckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(24))
            .build();

    // Cache for reactions: 100 requests per hour
    private final Cache<String, Bucket> reactionBuckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(1))
            .build();

    public Bucket resolvePostBucket(String key) {
        return postBuckets.get(key, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofHours(24))))
                .build());
    }

    public Bucket resolveReactionBucket(String key) {
        return reactionBuckets.get(key, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofHours(1))))
                .build());
    }
}
