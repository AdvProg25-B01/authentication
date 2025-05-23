package id.ac.ui.cs.advprog.authentication.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UpdatePasswordRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUpdatePasswordRequestGettersAndSetters() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("user@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("Newpass@1");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("oldPass", request.getOldPassword());
        assertEquals("Newpass@1", request.getNewPassword());
    }

    @Test
    void testNewPasswordCannotBeBlank() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("user@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword(" "); // blank

        Set<ConstraintViolation<UpdatePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password cannot be blank")));
    }

    @Test
    void testNewPasswordTooShort() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("user@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("P@1"); // too short

        Set<ConstraintViolation<UpdatePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("at least 8 characters")));
    }

    @Test
    void testNewPasswordMissingUppercaseNumberSpecialChar() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("user@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("password"); // weak

        Set<ConstraintViolation<UpdatePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must contain")));
    }

    @Test
    void testValidNewPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("user@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("Password@1"); // valid

        Set<ConstraintViolation<UpdatePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
