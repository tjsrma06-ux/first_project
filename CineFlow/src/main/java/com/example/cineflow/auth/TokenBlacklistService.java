package com.example.cineflow.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "auth:logout:";

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void revoke(String tokenId, Instant expiresAt) {
        Duration remainingLifetime = Duration.between(Instant.now(), expiresAt);
        if (!remainingLifetime.isPositive()) {
            return;
        }
        redisTemplate.opsForValue().set(KEY_PREFIX + tokenId, "logout", remainingLifetime);
    }

    public boolean isRevoked(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + tokenId));
    }
}
