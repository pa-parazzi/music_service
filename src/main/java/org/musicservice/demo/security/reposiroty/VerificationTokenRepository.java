package org.musicservice.demo.security.reposiroty;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query("select v_t from VerificationToken v_t join fetch v_t.user where v_t.token=:token")
    Optional<VerificationToken> findByToken(String token);

    void deleteAllByExpiryDateBefore(Instant expiryDateBefore);
}
