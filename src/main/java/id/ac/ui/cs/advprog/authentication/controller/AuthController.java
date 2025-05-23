package id.ac.ui.cs.advprog.authentication.controller;

import id.ac.ui.cs.advprog.authentication.dto.*;
import id.ac.ui.cs.advprog.authentication.exception.CustomException;
import id.ac.ui.cs.advprog.authentication.model.Token;
import id.ac.ui.cs.advprog.authentication.repository.TokenRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtUtil;
import id.ac.ui.cs.advprog.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        authService.logout(email);
        return ResponseEntity.ok("Logout successful.");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        if (!jwtUtil.validateToken(token, email)) {
            throw new CustomException("Invalid token");
        }
        return ResponseEntity.ok(authService.getProfile(email));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        Token token = tokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException("Invalid refresh token"));

        if (jwtUtil.validateRefreshToken(request.getRefreshToken())) {
            String newAccessToken = jwtUtil.generateAccessToken(token.getUser().getEmail());
            return ResponseEntity.ok(new AuthResponse(newAccessToken, request.getRefreshToken(), token.getUser().getRole().name()));
        } else {
            throw new CustomException("Refresh token expired");
        }
    }
}