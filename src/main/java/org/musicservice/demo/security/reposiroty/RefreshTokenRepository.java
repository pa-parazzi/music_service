package org.musicservice.demo.security.reposiroty;

import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.security.refreshToken.RefreshTokenProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findEntityByTokenHash(String tokenHash);

    Optional<RefreshTokenProjection> findForAuthByTokenHash(String tokenHash);

    void deleteAllByExpiryDateBefore(Instant expiryDateBefore);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from RefreshToken t where t.userId=:userId")
    void deleteByUserId(Long userId);
}
