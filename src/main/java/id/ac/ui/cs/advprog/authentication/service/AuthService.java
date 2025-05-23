package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.*;
import id.ac.ui.cs.advprog.authentication.model.Token;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.exception.CustomException;
import id.ac.ui.cs.advprog.authentication.repository.TokenRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtUtil;
import id.ac.ui.cs.advprog.authentication.service.factory.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserFactory userFactory;
    private final TokenRepository tokenRepository;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists");
        }

        User user = userFactory.createUser(request.getName(), request.getEmail(), request.getPassword(), request.getRole());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        Token token = Token.builder()
                .refreshToken(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + JwtUtil.REFRESH_EXPIRATION_TIME))
                .user(user)
                .build();
        tokenRepository.save(token);

        return new AuthResponse(accessToken, refreshToken, user.getRole().name());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid credentials"));

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        Token token = Token.builder()
                .refreshToken(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + JwtUtil.REFRESH_EXPIRATION_TIME))
                .user(user)
                .build();
        tokenRepository.save(token);

        return new AuthResponse(accessToken, refreshToken, user.getRole().name());
    }

    public String changePassword(UpdatePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password updated successfully";
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
        tokenRepository.deleteAllByUserId(user.getId());
    }

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
        return new UserProfileResponse(user.getName(), user.getEmail(), user.getRole().name());
    }
}
