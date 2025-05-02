package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.*;
import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.exception.CustomException;
import id.ac.ui.cs.advprog.authentication.model.User;
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
        request.setPassword("password123");
        request.setRole(Role.ADMIN);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        when(userFactory.createUser(request.getName(), request.getEmail(), request.getPassword(), request.getRole())).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("mockedToken");

        AuthResponse response = authService.register(request);

        assertEquals("mockedToken", response.getToken());
        assertEquals("ADMIN", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("johndoe@mail.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(CustomException.class, () -> authService.register(request));
    }

    @Test
    public void testLoginSuccess() {
        AuthRequest request = new AuthRequest();
        request.setEmail("johndoe@mail.com");
        request.setPassword("password123");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.KASIR)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("jwtToken");

        AuthResponse response = authService.login(request);

        assertEquals("jwtToken", response.getToken());
        assertEquals("KASIR", response.getRole());
    }

    @Test
    public void testLoginInvalidCredentials() {
        AuthRequest request = new AuthRequest();
        request.setEmail("wrong@mail.com");
        request.setPassword("wrongpass");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> authService.login(request));
    }

    @Test
    public void testChangePasswordSuccess() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("johndoe@mail.com");
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
    }

    @Test
    public void testChangePasswordWrongOldPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("johndoe@mail.com");
        request.setOldPassword("wrongOld");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedOldPass")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(CustomException.class, () -> authService.changePassword(request));
    }

    @Test
    public void testChangePasswordUserNotFound() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setEmail("nonexistent@mail.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> authService.changePassword(request));
    }
}