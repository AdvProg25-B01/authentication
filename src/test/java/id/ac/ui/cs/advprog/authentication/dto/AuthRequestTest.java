package id.ac.ui.cs.advprog.authentication.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthRequestTest {

    @Test
    void testAuthRequestGettersAndSetters() {
        AuthRequest request = new AuthRequest();
        request.setEmail("user@example.com");
        request.setPassword("securepassword");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("securepassword", request.getPassword());
    }
}
