package id.ac.ui.cs.advprog.authentication.repository;

import id.ac.ui.cs.advprog.authentication.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    void deleteByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    void deleteAllByUser_Id(Long userId);
}