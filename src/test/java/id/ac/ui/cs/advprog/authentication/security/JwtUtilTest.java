package id.ac.ui.cs.advprog.authentication.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

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
    void generateAccessToken_ValidUsername_ReturnsToken() {
        String token = jwtUtil.generateAccessToken(testUsername);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateRefreshToken_ValidUsername_ReturnsToken() {
        String token = jwtUtil.generateRefreshToken(testUsername);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        String token = jwtUtil.generateAccessToken(testUsername);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void extractExpiration_ValidToken_ReturnsFutureDate() {
        String token = jwtUtil.generateAccessToken(testUsername);

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractClaim_ReturnsCorrectClaim() {
        String token = jwtUtil.generateAccessToken(testUsername);
        Function<Claims, String> claimResolver = Claims::getSubject;

        String claim = jwtUtil.extractClaim(token, claimResolver);

        assertEquals(testUsername, claim);
    }

    @Test
    void validateToken_CorrectUsernameAndValidToken_ReturnsTrue() {
        String token = jwtUtil.generateAccessToken(testUsername);

        assertTrue(jwtUtil.validateToken(token, testUsername));
    }

    @Test
    void validateToken_WrongUsername_ReturnsFalse() {
        String token = jwtUtil.generateAccessToken(testUsername);

        assertFalse(jwtUtil.validateToken(token, "wrong@example.com"));
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000 * 60);

        Key key = Keys.hmacShaKeyFor(testSecret.getBytes());
        String expiredToken = Jwts.builder()
                .setSubject(testUsername)
                .setIssuedAt(new Date(now.getTime() - 1000 * 60 * 2))
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.validateToken(expiredToken, testUsername));
    }

    @Test
    void validateRefreshToken_ValidToken_ReturnsTrue() {
        String refreshToken = jwtUtil.generateRefreshToken(testUsername);

        assertTrue(jwtUtil.validateRefreshToken(refreshToken));
    }

    @Test
    void validateRefreshToken_ExpiredToken_ReturnsFalse() {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000 * 60 * 60);

        Key key = Keys.hmacShaKeyFor(testSecret.getBytes());
        String expiredRefreshToken = Jwts.builder()
                .setSubject(testUsername)
                .setIssuedAt(new Date(now.getTime() - 1000 * 60 * 120))
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.validateRefreshToken(expiredRefreshToken));
    }

    @Test
    void validateToken_MalformedToken_ReturnsFalse() {
        String malformedToken = "this.is.not.valid";

        assertFalse(jwtUtil.validateToken(malformedToken, testUsername));
    }

    @Test
    void validateRefreshToken_MalformedToken_ReturnsFalse() {
        String malformedToken = "this.is.also.not.valid";

        assertFalse(jwtUtil.validateRefreshToken(malformedToken));
    }
}
