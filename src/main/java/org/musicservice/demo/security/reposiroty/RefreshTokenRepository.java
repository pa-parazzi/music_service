package org.musicservice.demo.security.reposiroty;

import org.musicservice.demo.entity.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByExpiryDateBefore(Instant expiryDateBefore);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from RefreshToken t where t.userId=:userId")
    void deleteByUserId(Long userId);

    @Modifying
    @Query("update RefreshToken t set t.tokenHash=:hash, t.expiryDate=:expiryDate, t.revoked=false where t.userId=:userId")
    void rotation(@Param("userId") Long userId, @Param("hash") String hash, @Param("expiryDate") Instant expiryDate);
}
