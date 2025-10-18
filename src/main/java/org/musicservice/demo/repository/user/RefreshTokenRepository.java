package org.musicservice.demo.repository.user;

import org.musicservice.demo.model.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken findByUserId(Long userId);

    void deleteAllByExpiryDateBefore(Instant expiryDateBefore);

    List<RefreshToken> findALlByExpiryDateBefore(Instant expiryDateBefore);
}
