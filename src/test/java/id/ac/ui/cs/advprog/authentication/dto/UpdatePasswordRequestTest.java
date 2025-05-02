package id.ac.ui.cs.advprog.authentication.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UpdatePasswordRequestTest {

    @Test
    void testUpdatePasswordRequestGettersAndSetters() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("user@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("oldPass", request.getOldPassword());
        assertEquals("newPass", request.getNewPassword());
    }
}
