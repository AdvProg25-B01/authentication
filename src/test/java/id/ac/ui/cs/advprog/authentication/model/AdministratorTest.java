package id.ac.ui.cs.advprog.authentication.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class AdministratorTest {
    @Test
    void testAdministratorGettersAndSetters() {
        Administrator administrator = new Administrator();
        administrator.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        administrator.setFullName("Admin Name");
        administrator.setEmail("administrator@example.com");
        administrator.setPhoneNumber("1234567890");
        administrator.setPassword("secret");

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), administrator.getId());
        assertEquals("Admin Name", administrator.getFullName());
        assertEquals("administrator@example.com", administrator.getEmail());
        assertEquals("1234567890", administrator.getPhoneNumber());
        assertEquals("secret", administrator.getPassword());
    }

    @Test
    void testAdministratorToString() {
        Administrator administrator = new Administrator("Administrator Name", "administrator@example.com", "1234567890", "secret");
        administrator.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        String toString = administrator.toString();
        assertTrue(toString.contains("Admin"));
        assertTrue(toString.contains("admin@example.com"));
    }
}
