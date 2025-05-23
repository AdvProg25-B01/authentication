package id.ac.ui.cs.advprog.authentication.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        AuthResponse response = new AuthResponse("access-token", "refresh-token", "admin");

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("admin", response.getRole());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        AuthResponse response = new AuthResponse();
        response.setAccessToken("access-token");
        response.setRefreshToken("refresh-token");
        response.setRole("user");

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("user", response.getRole());
    }

    @Test
    void testBuilder() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("builder-access")
                .refreshToken("builder-refresh")
                .role("builder-role")
                .build();

        assertEquals("builder-access", response.getAccessToken());
        assertEquals("builder-refresh", response.getRefreshToken());
        assertEquals("builder-role", response.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthResponse response1 = new AuthResponse("access", "refresh", "role");
        AuthResponse response2 = new AuthResponse("access", "refresh", "role");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        AuthResponse response = new AuthResponse("access", "refresh", "admin");
        String result = response.toString();

        assertTrue(result.contains("access"));
        assertTrue(result.contains("refresh"));
        assertTrue(result.contains("admin"));
    }
}
