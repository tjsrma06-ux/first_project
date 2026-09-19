package com.example.cineflow.auth;

import com.example.cineflow.domain.user.UserRole;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(new JwtProperties(
            "bG9jYWwtdGVzdC1qd3Qtc2VjcmV0LWtleS1tdXN0LWJlLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=",
            1800,
            "cineflow-test"
    ));

    JwtTokenProviderTest() {
        tokenProvider.initialize();
    }

    @Test
    void createsAndParsesSignedToken() {
        JwtTokenProvider.IssuedToken token = tokenProvider.createToken(1L, "user@example.com", UserRole.CUSTOMER);

        JwtTokenProvider.TokenClaims claims = tokenProvider.parse(token.value());

        assertThat(claims.userId()).isEqualTo(1L);
        assertThat(claims.email()).isEqualTo("user@example.com");
        assertThat(claims.role()).isEqualTo(UserRole.CUSTOMER);
        assertThat(claims.tokenId()).isNotBlank();
    }

    @Test
    void rejectsTamperedToken() {
        JwtTokenProvider.IssuedToken token = tokenProvider.createToken(1L, "user@example.com", UserRole.CUSTOMER);

        assertThatThrownBy(() -> tokenProvider.parse(token.value() + "tampered"))
                .isInstanceOf(JwtException.class);
    }
}
