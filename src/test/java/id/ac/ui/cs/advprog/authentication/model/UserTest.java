package id.ac.ui.cs.advprog.authentication.model;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserBuilderAndGetters() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("securepassword")
                .role(Role.ADMIN)
                .active(true)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("securepassword", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void testSetters() {
        User user = new User();
        user.setId(2L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("anotherpassword");
        user.setRole(Role.KASIR);
        user.setActive(false);

        assertEquals(2L, user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("anotherpassword", user.getPassword());
        assertEquals(Role.KASIR, user.getRole());
        assertFalse(user.isActive());
    }
}
