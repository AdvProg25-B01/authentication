package id.ac.ui.cs.advprog.authentication.controller;

import id.ac.ui.cs.advprog.authentication.dto.*;
import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("testuser@mail.com");
        request.setPassword("testpassword");
        request.setRole(Role.ADMIN);

        AuthResponse mockResponse = new AuthResponse("mockedToken", "ADMIN");

        // Mock behavior
        when(authService.register(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthResponse> responseEntity = authController.register(request);

        // Assert
        assertNotNull(responseEntity, "Response should not be null");
        assertEquals(200, responseEntity.getStatusCode().value(), "Status code should be 200");
        assertNotNull(responseEntity.getBody(), "Response body should not be null");
        assertEquals("mockedToken", responseEntity.getBody().getToken(), "Token should match");
        assertEquals("ADMIN", responseEntity.getBody().getRole(), "Role should match");
    }

    @Test
    public void testLogin() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("testuser@mail.com");
        request.setPassword("testpassword");

        AuthResponse mockResponse = new AuthResponse("mockedToken", "KASIR");

        // Mock behavior
        when(authService.login(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthResponse> responseEntity = authController.login(request);

        // Assert
        assertNotNull(responseEntity, "Response should not be null");
        assertEquals(200, responseEntity.getStatusCode().value(), "Status code should be 200");
        assertNotNull(responseEntity.getBody(), "Response body should not be null");
        assertEquals("mockedToken", responseEntity.getBody().getToken(), "Token should match");
        assertEquals("KASIR", responseEntity.getBody().getRole(), "Role should match");
    }

    @Test
    public void testChangePassword() {
        // Arrange
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("testuser@mail.com");
        request.setOldPassword("oldpassword");
        request.setNewPassword("newpassword");

        // Mock behavior
        when(authService.changePassword(request)).thenReturn("Password updated successfully");

        // Act
        ResponseEntity<String> responseEntity = authController.changePassword(request);

        // Assert
        assertNotNull(responseEntity, "Response should not be null");
        assertEquals(200, responseEntity.getStatusCode().value(), "Status code should be 200");
        assertNotNull(responseEntity.getBody(), "Response body should not be null");
        assertEquals("Password updated successfully", responseEntity.getBody(), "Response body should match");
    }

    @Test
    public void testLogout() {
        // Act
        ResponseEntity<String> responseEntity = authController.logout();

        // Assert
        assertNotNull(responseEntity, "Response should not be null");
        assertEquals(200, responseEntity.getStatusCode().value(), "Status code should be 200");
        assertNotNull(responseEntity.getBody(), "Response body should not be null");
        assertEquals("Logout successful.", responseEntity.getBody(), "Response body should match");
    }
}
