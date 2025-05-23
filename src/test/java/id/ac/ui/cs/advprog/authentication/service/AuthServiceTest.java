package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.*;
import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.exception.CustomException;
import id.ac.ui.cs.advprog.authentication.model.Token;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.TokenRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtUtil;
import id.ac.ui.cs.advprog.authentication.service.factory.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserFactory userFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("johndoe@mail.com");
        request.setPassword("Password123!");
        request.setRole(Role.ADMIN);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.ADMIN)
                .build();

        when(userFactory.createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        )).thenReturn(user);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateAccessToken(user.getEmail())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user.getEmail())).thenReturn("refreshToken");

        AuthResponse response = authService.register(request);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("ADMIN", response.getRole());
        verify(userRepository).save(user);
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    public void testRegisterEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("existing@mail.com");
        request.setPassword("Password123!");
        request.setRole(Role.ADMIN);

        User existingUser = User.builder()
                .email(request.getEmail())
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        CustomException exception = assertThrows(CustomException.class, () -> authService.register(request));
        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    public void testLoginSuccess() {
        AuthRequest request = new AuthRequest();
        request.setEmail("johndoe@mail.com");
        request.setPassword("Password123!");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.KASIR)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user.getEmail())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user.getEmail())).thenReturn("refreshToken");

        AuthResponse response = authService.login(request);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("KASIR", response.getRole());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    public void testLoginInvalidCredentials() {
        AuthRequest request = new AuthRequest();
        request.setEmail("wrong@mail.com");
        request.setPassword("wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", exception.getMessage());

        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    public void testChangePasswordSuccess() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("test@mail.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedOldPass")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPass");

        String response = authService.changePassword(request);

        assertEquals("Password updated successfully", response);
        verify(userRepository).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    public void testChangePasswordUserNotFound() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("nonexistent@mail.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.changePassword(request));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangePasswordIncorrectOldPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("test@mail.com");
        request.setOldPassword("wrongOldPass");
        request.setNewPassword("newPass");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedOldPass")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> authService.changePassword(request));
        assertEquals("Old password is incorrect", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(request.getNewPassword());
    }

    @Test
    public void testLogoutSuccess() {
        String email = "test@mail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authService.logout(email);

        verify(tokenRepository).deleteAllByUser_Id(1L);
    }

    @Test
    public void testLogoutUserNotFound() {
        String email = "nonexistent@mail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.logout(email));
        assertEquals("User not found", exception.getMessage());

        verify(tokenRepository, never()).deleteAllByUser_Id(anyLong());
    }

    @Test
    public void testGetProfileSuccess() {
        String email = "test@mail.com";
        User user = User.builder()
                .name("John Doe")
                .email(email)
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserProfileResponse response = authService.getProfile(email);

        assertEquals("John Doe", response.getName());
        assertEquals(email, response.getEmail());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    public void testGetProfileUserNotFound() {
        String email = "nonexistent@mail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.getProfile(email));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetProfileWithKasirRole() {
        String email = "kasir@mail.com";
        User user = User.builder()
                .name("Kasir User")
                .email(email)
                .role(Role.KASIR)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserProfileResponse response = authService.getProfile(email);

        assertEquals("Kasir User", response.getName());
        assertEquals(email, response.getEmail());
        assertEquals("KASIR", response.getRole());
    }

    @Test
    public void testRegisterWithKasirRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Kasir User");
        request.setEmail("kasir@mail.com");
        request.setPassword("Password123!");
        request.setRole(Role.KASIR);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.KASIR)
                .build();

        when(userFactory.createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        )).thenReturn(user);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateAccessToken(user.getEmail())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user.getEmail())).thenReturn("refreshToken");

        AuthResponse response = authService.register(request);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("KASIR", response.getRole());
        verify(userRepository).save(user);
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    public void testLoginWithAdminRole() {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@mail.com");
        request.setPassword("Password123!");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user.getEmail())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user.getEmail())).thenReturn("refreshToken");

        AuthResponse response = authService.login(request);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("ADMIN", response.getRole());
        verify(tokenRepository).save(any(Token.class));
    }
}