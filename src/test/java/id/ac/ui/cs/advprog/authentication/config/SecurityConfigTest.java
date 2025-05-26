package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.authentication.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoderShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.matches("password", passwordEncoder.encode("password")));
    }

    @Test
    void authenticationManagerShouldReturnFromConfiguration() throws Exception {
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertEquals(expectedManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void corsConfigurationSourceShouldReturnValidConfiguration() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(mockRequest);
        assertNotNull(config);

        List<String> allowedOrigins = config.getAllowedOrigins();
        List<String> allowedMethods = config.getAllowedMethods();

        assertTrue(allowedOrigins.contains("http://localhost:3000"));
        assertTrue(allowedOrigins.contains("https://main.d2mcs3ch2l35ck.amplifyapp.com"));
        assertTrue(allowedMethods.contains("GET"));
        assertTrue(allowedMethods.contains("POST"));
        assertTrue(allowedMethods.contains("OPTIONS"));

        assertEquals(List.of("*"), config.getAllowedHeaders());
        assertTrue(Boolean.TRUE.equals(config.getAllowCredentials()));
    }
}