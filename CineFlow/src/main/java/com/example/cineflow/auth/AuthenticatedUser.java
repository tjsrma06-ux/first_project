package com.example.cineflow.auth;

import com.example.cineflow.domain.user.UserRole;

import java.time.Instant;

public record AuthenticatedUser(
        Long id,
        String email,
        UserRole role,
        String tokenId,
        Instant tokenExpiresAt
) {
}
