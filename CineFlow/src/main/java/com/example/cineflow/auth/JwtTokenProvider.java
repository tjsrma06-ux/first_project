package com.example.cineflow.auth;

import com.example.cineflow.domain.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private SecretKey signingKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void initialize() {
        if (properties.secret() == null || properties.secret().isBlank()) {
            throw new IllegalStateException("JWT_SECRET 환경 변수가 설정되지 않았습니다.");
        }
        if (properties.accessTokenExpirationSeconds() <= 0) {
            throw new IllegalStateException("JWT_ACCESS_TOKEN_EXPIRATION_SECONDS는 0보다 커야 합니다.");
        }
        if (properties.issuer() == null || properties.issuer().isBlank()) {
            throw new IllegalStateException("JWT_ISSUER 환경 변수가 설정되지 않았습니다.");
        }

        try {
            signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("JWT_SECRET은 32바이트 이상인 Base64 값이어야 합니다.", exception);
        }
    }

    public IssuedToken createToken(Long userId, String email, UserRole role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.accessTokenExpirationSeconds());

        String token = Jwts.builder()
                .issuer(properties.issuer())
                .subject(email)
                .id(UUID.randomUUID().toString())
                .claim("userId", userId)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        return new IssuedToken(token, expiresAt);
    }

    public TokenClaims parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = claims.get("userId", Long.class);
        String roleName = claims.get("role", String.class);
        if (userId == null || roleName == null || claims.getId() == null || claims.getExpiration() == null) {
            throw new IllegalArgumentException("필수 JWT 정보가 없습니다.");
        }

        return new TokenClaims(
                userId,
                claims.getSubject(),
                UserRole.valueOf(roleName),
                claims.getId(),
                claims.getExpiration().toInstant()
        );
    }

    public record IssuedToken(String value, Instant expiresAt) {
    }

    public record TokenClaims(Long userId, String email, UserRole role, String tokenId, Instant expiresAt) {
    }
}
