package org.musicservice.demo.security.reposiroty;

import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.security.refreshToken.RefreshTokenProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findEntityByTokenHash(String tokenHash);

    Optional<RefreshTokenProjection> findForAuthByTokenHash(String tokenHash);

    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    List<RefreshToken> findALlByExpiryDateBefore(Instant expiryDateBefore);
}
