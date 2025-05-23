package id.ac.ui.cs.advprog.authentication.repository;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.model.Token;
import id.ac.ui.cs.advprog.authentication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Nadhifa")
                .email("nadhifa@example.com")
                .password("secure")
                .role(Role.ADMIN)
                .active(true)
                .build();

        user = userRepository.save(user);
    }

    @Test
    void testFindByRefreshToken() {
        Token token = Token.builder()
                .refreshToken("abc123")
                .expiryDate(new Date())
                .user(user)
                .build();
        tokenRepository.save(token);

        Optional<Token> found = tokenRepository.findByRefreshToken("abc123");
        assertTrue(found.isPresent());
        assertEquals("abc123", found.get().getRefreshToken());
    }

    @Test
    void testDeleteByRefreshToken() {
        Token token = Token.builder()
                .refreshToken("to-delete")
                .expiryDate(new Date())
                .user(user)
                .build();
        tokenRepository.save(token);

        tokenRepository.deleteByRefreshToken("to-delete");

        Optional<Token> found = tokenRepository.findByRefreshToken("to-delete");
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteAllByUserId() {
        Token token1 = Token.builder()
                .refreshToken("token1")
                .expiryDate(new Date())
                .user(user)
                .build();

        Token token2 = Token.builder()
                .refreshToken("token2")
                .expiryDate(new Date())
                .user(user)
                .build();

        tokenRepository.save(token1);
        tokenRepository.save(token2);

        tokenRepository.deleteAllByUserId(user.getId());

        assertTrue(tokenRepository.findByRefreshToken("token1").isEmpty());
        assertTrue(tokenRepository.findByRefreshToken("token2").isEmpty());
    }
}
