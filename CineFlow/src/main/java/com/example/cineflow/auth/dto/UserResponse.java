package com.example.cineflow.auth.dto;

import com.example.cineflow.domain.user.User;
import com.example.cineflow.domain.user.UserRole;

public record UserResponse(
        Long id,
        String email,
        String name,
        String phoneNumber,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber(), user.getRole());
    }
}
