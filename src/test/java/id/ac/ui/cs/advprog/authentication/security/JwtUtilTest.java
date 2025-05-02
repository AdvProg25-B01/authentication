package id.ac.ui.cs.advprog.authentication.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String testUsername = "test@example.com";
    private final String testSecret = "thisIsAVeryLongTestSecretKeyForJwtTestingPurposes123456789012345678901234";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", testSecret);
    }

    @Test
    void generateToken_ValidUsername_ReturnsToken() {
        // Act
        String token = jwtUtil.generateToken(testUsername);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void validateToken_ValidTokenAndUsername_ReturnsTrue() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername);

        // Act
        boolean isValid = jwtUtil.validateToken(token, testUsername);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidUsername_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "wrong@example.com");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractExpiration_ValidToken_ReturnsExpirationDate() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername);

        // Act
        Date expirationDate = jwtUtil.extractExpiration(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void extractClaim_ValidToken_ReturnsClaim() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername);
        Function<Claims, String> claimsResolver = Claims::getSubject;

        // Act
        String subject = jwtUtil.extractClaim(token, claimsResolver);

        // Assert
        assertEquals(testUsername, subject);
    }
}