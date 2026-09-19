package com.example.cineflow.auth;

import com.example.cineflow.auth.dto.AuthResponse;
import com.example.cineflow.auth.dto.LoginRequest;
import com.example.cineflow.auth.dto.SignupRequest;
import com.example.cineflow.auth.dto.UserResponse;
import com.example.cineflow.domain.user.User;
import com.example.cineflow.domain.user.UserRepository;
import com.example.cineflow.domain.user.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name().trim())
                .phoneNumber(normalizeOptionalValue(request.phoneNumber()))
                .role(UserRole.CUSTOMER)
                .build();

        return createAuthResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        return createAuthResponse(user);
    }

    public void logout(AuthenticatedUser authenticatedUser) {
        tokenBlacklistService.revoke(authenticatedUser.tokenId(), authenticatedUser.tokenExpiresAt());
    }

    @Transactional
    public void withdraw(AuthenticatedUser authenticatedUser, String password) {
        User user = userRepository.findById(authenticatedUser.id())
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw invalidCredentials();
        }

        tokenBlacklistService.revoke(authenticatedUser.tokenId(), authenticatedUser.tokenExpiresAt());
        userRepository.delete(user);
    }

    private AuthResponse createAuthResponse(User user) {
        JwtTokenProvider.IssuedToken token = tokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token.value(), "Bearer", token.expiresAt(), UserResponse.from(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeOptionalValue(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
