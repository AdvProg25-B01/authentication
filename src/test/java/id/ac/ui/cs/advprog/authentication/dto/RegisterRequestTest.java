package id.ac.ui.cs.advprog.authentication.dto;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    @Test
    void testRegisterRequestGettersAndSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setRole(Role.ADMIN);

        assertEquals("John Doe", request.getName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals(Role.ADMIN, request.getRole());
    }
}
