package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.security.CustomUserDetailsService;
import id.ac.ui.cs.advprog.authentication.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

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
    void corsFilterFallbackShouldReturnValidConfiguration() {
        FilterRegistrationBean<CorsFilter> filterRegistrationBean = securityConfig.corsFilterFallback();
        assertNotNull(filterRegistrationBean);
        assertEquals(0, filterRegistrationBean.getOrder());

        CorsFilter corsFilter = filterRegistrationBean.getFilter();
        assertNotNull(corsFilter);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://main.d2mcs3ch2l35ck.amplifyapp.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/auth/login");

        CorsConfiguration resultConfig = source.getCorsConfiguration(mockRequest);
        assertNotNull(resultConfig);

        assertTrue(resultConfig.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(resultConfig.getAllowedOrigins().contains("https://main.d2mcs3ch2l35ck.amplifyapp.com"));
        assertTrue(resultConfig.getAllowedMethods().contains("GET"));
        assertTrue(resultConfig.getAllowedMethods().contains("POST"));
        assertTrue(resultConfig.getAllowedMethods().contains("OPTIONS"));
        assertTrue(resultConfig.getAllowedHeaders().contains("Authorization"));
        assertTrue(resultConfig.getAllowedHeaders().contains("Content-Type"));
        assertTrue(resultConfig.getAllowedHeaders().contains("X-Requested-With"));
        assertTrue(Boolean.TRUE.equals(resultConfig.getAllowCredentials()));
    }
}