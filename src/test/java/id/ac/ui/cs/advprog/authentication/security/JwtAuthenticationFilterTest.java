package id.ac.ui.cs.advprog.authentication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;
    private final String testEmail = "test@example.com";
    private final String testToken = "testJwtToken";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = new User(testEmail, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void doFilterInternal_NoAuthHeader_ContinuesFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_InvalidAuthHeaderFormat_ContinuesFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat " + testToken);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtUtil.extractUsername(testToken)).thenReturn(testEmail);
        when(userDetailsService.loadUserByUsername(testEmail)).thenReturn(userDetails);
        when(jwtUtil.validateToken(testToken, testEmail)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, times(1)).extractUsername(testToken);
        verify(userDetailsService, times(1)).loadUserByUsername(testEmail);
        verify(jwtUtil, times(1)).validateToken(testToken, testEmail);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(testEmail, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtUtil.extractUsername(testToken)).thenReturn(testEmail);
        when(userDetailsService.loadUserByUsername(testEmail)).thenReturn(userDetails);
        when(jwtUtil.validateToken(testToken, testEmail)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, times(1)).extractUsername(testToken);
        verify(userDetailsService, times(1)).loadUserByUsername(testEmail);
        verify(jwtUtil, times(1)).validateToken(testToken, testEmail);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_NullUsername_DoesNotCallUserDetailsService() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtUtil.extractUsername(testToken)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, times(1)).extractUsername(testToken);
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}