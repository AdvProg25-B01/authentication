package id.ac.ui.cs.advprog.authentication.controller;

import id.ac.ui.cs.advprog.authentication.dto.*;
import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.exception.CustomException;
import id.ac.ui.cs.advprog.authentication.model.Token;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.TokenRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtUtil;
import id.ac.ui.cs.advprog.authentication.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenRepository tokenRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setRole(Role.ADMIN);

        AuthResponse mockResponse = AuthResponse.builder()
                .accessToken("access123")
                .refreshToken("refresh456")
                .role("ADMIN")
                .build();

        when(authService.register(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("access123", response.getBody().getAccessToken());
        assertEquals("refresh456", response.getBody().getRefreshToken());
        assertEquals("ADMIN", response.getBody().getRole());
    }

    @Test
    public void testLogin() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123!");

        AuthResponse mockResponse = AuthResponse.builder()
                .accessToken("access789")
                .refreshToken("refresh012")
                .role("KASIR")
                .build();

        when(authService.login(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("access789", response.getBody().getAccessToken());
        assertEquals("refresh012", response.getBody().getRefreshToken());
        assertEquals("KASIR", response.getBody().getRole());
    }

    @Test
    public void testChangePassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("test@example.com");
        request.setOldPassword("OldPass123!");
        request.setNewPassword("NewPass456!");

        when(authService.changePassword(request)).thenReturn("Password updated successfully");

        ResponseEntity<String> response = authController.changePassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password updated successfully", response.getBody());
    }

    @Test
    public void testLogout() {
        String authHeader = "Bearer testToken";
        String email = "test@example.com";

        when(jwtUtil.extractUsername("testToken")).thenReturn(email);

        ResponseEntity<String> response = authController.logout(authHeader);

        verify(authService).logout(email);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful.", response.getBody());
    }

    @Test
    public void testGetProfile() {
        String authHeader = "Bearer testToken";
        String email = "test@example.com";

        UserProfileResponse mockProfile = new UserProfileResponse();
        mockProfile.setEmail(email);
        mockProfile.setRole("ADMIN");

        when(jwtUtil.extractUsername("testToken")).thenReturn(email);
        when(jwtUtil.validateToken("testToken", email)).thenReturn(true);
        when(authService.getProfile(email)).thenReturn(mockProfile);

        ResponseEntity<UserProfileResponse> response = authController.getProfile(authHeader);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(email, response.getBody().getEmail());
        assertEquals("ADMIN", response.getBody().getRole());
    }

    @Test
    public void testGetProfileInvalidToken() {
        String authHeader = "Bearer invalidToken";
        String email = "test@example.com";

        when(jwtUtil.extractUsername("invalidToken")).thenReturn(email);
        when(jwtUtil.validateToken("invalidToken", email)).thenReturn(false);

        assertThrows(CustomException.class, () -> {
            authController.getProfile(authHeader);
        });
    }

    @Test
    public void testRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh123");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.ADMIN);

        Token mockToken = new Token();
        mockToken.setRefreshToken("refresh123");
        mockToken.setUser(mockUser);

        AuthResponse expectedResponse = new AuthResponse("newAccessToken123", "refresh123", "ADMIN");

        when(tokenRepository.findByRefreshToken("refresh123")).thenReturn(Optional.of(mockToken));
        when(jwtUtil.validateRefreshToken("refresh123")).thenReturn(true);
        when(jwtUtil.generateAccessToken("test@example.com")).thenReturn("newAccessToken123");

        ResponseEntity<AuthResponse> response = authController.refreshToken(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newAccessToken123", response.getBody().getAccessToken());
        assertEquals("refresh123", response.getBody().getRefreshToken());
        assertEquals("ADMIN", response.getBody().getRole());
    }

    @Test
    public void testRefreshTokenInvalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalidRefresh");

        when(tokenRepository.findByRefreshToken("invalidRefresh")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> {
            authController.refreshToken(request);
        });
    }

    @Test
    public void testRefreshTokenExpired() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("expiredRefresh");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.ADMIN);

        Token mockToken = new Token();
        mockToken.setRefreshToken("expiredRefresh");
        mockToken.setUser(mockUser);

        when(tokenRepository.findByRefreshToken("expiredRefresh")).thenReturn(Optional.of(mockToken));
        when(jwtUtil.validateRefreshToken("expiredRefresh")).thenReturn(false);

        assertThrows(CustomException.class, () -> {
            authController.refreshToken(request);
        });
    }
}
