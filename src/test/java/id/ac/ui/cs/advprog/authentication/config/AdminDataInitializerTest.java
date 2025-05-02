package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminDataInitializer adminDataInitializer;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void whenNoAdminExists_shouldCreateDefaultAdmin() throws Exception {
        // Arrange
        String defaultAdminEmail = "admin@example.com";
        String defaultAdminPassword = "admin123";
        String encodedPassword = "encodedPassword";

        when(userRepository.findByEmail(defaultAdminEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(defaultAdminPassword)).thenReturn(encodedPassword);

        // Act
        adminDataInitializer.run();

        // Assert
        verify(userRepository).findByEmail(defaultAdminEmail);
        verify(passwordEncoder).encode(defaultAdminPassword);

        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(defaultAdminEmail) &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getRole() == Role.ADMIN &&
                        user.isActive()
        ));
    }

    @Test
    void whenAdminExists_shouldNotCreateDefaultAdmin() throws Exception {
        // Arrange
        String defaultAdminEmail = "admin@example.com";
        User existingAdmin = User.builder()
                .email(defaultAdminEmail)
                .password("existingEncodedPassword")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findByEmail(defaultAdminEmail)).thenReturn(Optional.of(existingAdmin));

        // Act
        adminDataInitializer.run();

        // Assert
        verify(userRepository).findByEmail(defaultAdminEmail);
        verifyNoMoreInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }
}