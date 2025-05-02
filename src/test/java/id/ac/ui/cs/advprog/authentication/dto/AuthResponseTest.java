package id.ac.ui.cs.advprog.authentication.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseTest {

    @Test
    void testAuthResponseConstructorAndGetters() {
        AuthResponse response = new AuthResponse("jwt-token", "admin");

        assertEquals("jwt-token", response.getToken());
        assertEquals("admin", response.getRole());
    }
}
