package com.moliveira.demo_park_api.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilsTest {

    private static final String SECRET = "first-test-signing-key-0123456789";
    private static final String OTHER_SECRET = "second-test-signing-key-012345678";

    @Test
    void acceptsTokenSignedWithTheConfiguredKey() {
        JwtUtils jwtUtils = new JwtUtils(SECRET);
        String token = JwtUtils.JWT_BEARER + jwtUtils.createToken("ana@email.com", "ADMIN").getToken();

        assertThat(jwtUtils.isTokenValid(token)).isTrue();
        assertThat(jwtUtils.getUsernameFromToken(token)).isEqualTo("ana@email.com");
    }

    @Test
    void rejectsTokenSignedWithADifferentKey() {
        String forged = JwtUtils.JWT_BEARER + new JwtUtils(OTHER_SECRET).createToken("ana@email.com", "ADMIN").getToken();

        assertThat(new JwtUtils(SECRET).isTokenValid(forged)).isFalse();
    }

    @Test
    void refusesToStartWithAMissingOrShortKey() {
        assertThatThrownBy(() -> new JwtUtils("too-short")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new JwtUtils("")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new JwtUtils(null)).isInstanceOf(IllegalStateException.class);
    }
}
