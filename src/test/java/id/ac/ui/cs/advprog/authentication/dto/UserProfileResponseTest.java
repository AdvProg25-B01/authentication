package id.ac.ui.cs.advprog.authentication.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UserProfileResponse response = new UserProfileResponse("Shofi", "shofi@example.com", "admin");

        assertEquals("Shofi", response.getName());
        assertEquals("shofi@example.com", response.getEmail());
        assertEquals("admin", response.getRole());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UserProfileResponse response = new UserProfileResponse();
        response.setName("Ayu");
        response.setEmail("ayu@example.com");
        response.setRole("user");

        assertEquals("Ayu", response.getName());
        assertEquals("ayu@example.com", response.getEmail());
        assertEquals("user", response.getRole());
    }

    @Test
    void testBuilder() {
        UserProfileResponse response = UserProfileResponse.builder()
                .name("Budi")
                .email("budi@example.com")
                .role("moderator")
                .build();

        assertEquals("Budi", response.getName());
        assertEquals("budi@example.com", response.getEmail());
        assertEquals("moderator", response.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfileResponse res1 = new UserProfileResponse("Ana", "ana@example.com", "admin");
        UserProfileResponse res2 = new UserProfileResponse("Ana", "ana@example.com", "admin");

        assertEquals(res1, res2);
        assertEquals(res1.hashCode(), res2.hashCode());
    }

    @Test
    void testToString() {
        UserProfileResponse response = new UserProfileResponse("Rina", "rina@example.com", "user");

        String str = response.toString();
        assertTrue(str.contains("Rina"));
        assertTrue(str.contains("rina@example.com"));
        assertTrue(str.contains("user"));
    }
}
