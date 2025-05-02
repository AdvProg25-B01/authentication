package id.ac.ui.cs.advprog.authentication.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private Key getSigningKey() {
    }

    public String extractUsername(String token) {
    }

    public Date extractExpiration(String token) {
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    }

    private Claims extractAllClaims(String token) {
    }

    private boolean isTokenExpired(String token) {
    }

    public String generateToken(String username) {
    }

    public boolean validateToken(String token, String username) {
    }
}
