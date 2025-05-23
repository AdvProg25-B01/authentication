package id.ac.ui.cs.advprog.authentication.dto;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRegisterRequestGettersAndSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setRole(Role.ADMIN);

        assertEquals("John Doe", request.getName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("Password@123", request.getPassword());
        assertEquals(Role.ADMIN, request.getRole());
    }

    @Test
    void testValidationFailsWhenNameIsBlank() {
        RegisterRequest request = new RegisterRequest();
        request.setName(" ");
        request.setEmail("valid@mail.com");
        request.setPassword("Password@123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
    }

    @Test
    void testValidationFailsWhenEmailInvalid() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Valid Name");
        request.setEmail("invalid-email");
        request.setPassword("Password@123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email must be valid")));
    }

    @Test
    void testValidationFailsWhenPasswordTooShort() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Valid Name");
        request.setEmail("valid@mail.com");
        request.setPassword("P@1"); // too short

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("at least 8 characters")));
    }

    @Test
    void testValidationFailsWhenPasswordMissingUppercaseNumberOrSpecialChar() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Valid Name");
        request.setEmail("valid@mail.com");
        request.setPassword("password"); // all lowercase, no number, no special char

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must contain")));
    }

    @Test
    void testValidationPassesForValidInput() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Valid Name");
        request.setEmail("valid@mail.com");
        request.setPassword("Password@1");
        request.setRole(Role.KASIR);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
