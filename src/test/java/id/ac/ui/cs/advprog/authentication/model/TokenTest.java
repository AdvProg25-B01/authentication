package id.ac.ui.cs.advprog.authentication.model;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        User user = User.builder()
                .id(1L)
                .name("Nadhifa")
                .email("nadhifa@example.com")
                .password("secret")
                .role(Role.ADMIN)
                .active(true)
                .build();

        Date expiry = new Date();

        Token token = new Token(100L, "refresh-token-abc", expiry, user);

        assertEquals(100L, token.getId());
        assertEquals("refresh-token-abc", token.getRefreshToken());
        assertEquals(expiry, token.getExpiryDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = User.builder()
                .id(2L)
                .name("Ayu")
                .email("ayu@example.com")
                .password("123")
                .role(Role.KASIR)
                .active(false)
                .build();

        Date expiry = new Date();

        Token token = new Token();
        token.setId(200L);
        token.setRefreshToken("refresh-token-xyz");
        token.setExpiryDate(expiry);
        token.setUser(user);

        assertEquals(200L, token.getId());
        assertEquals("refresh-token-xyz", token.getRefreshToken());
        assertEquals(expiry, token.getExpiryDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testBuilder() {
        User user = User.builder()
                .id(3L)
                .name("Budi")
                .email("budi@example.com")
                .password("pass123")
                .role(Role.KASIR)
                .active(true)
                .build();

        Date expiry = new Date();

        Token token = Token.builder()
                .id(300L)
                .refreshToken("refresh-builder")
                .expiryDate(expiry)
                .user(user)
                .build();

        assertEquals(300L, token.getId());
        assertEquals("refresh-builder", token.getRefreshToken());
        assertEquals(expiry, token.getExpiryDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testEqualsAndHashCode() {
        User user = User.builder()
                .id(4L)
                .name("Ana")
                .email("ana@example.com")
                .password("pass456")
                .role(Role.KASIR)
                .active(true)
                .build();

        Date expiry = new Date();

        Token token1 = new Token(1L, "token", expiry, user);
        Token token2 = new Token(1L, "token", expiry, user);

        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    void testToString() {
        User user = User.builder()
                .id(5L)
                .name("Rina")
                .email("rina@example.com")
                .password("pw")
                .role(Role.ADMIN)
                .active(true)
                .build();

        Token token = new Token(10L, "token-string", new Date(), user);
        String result = token.toString();

        assertTrue(result.contains("token-string"));

        assertTrue(result.contains("Rina"));
        assertTrue(result.contains("rina@example.com"));
    }
}
