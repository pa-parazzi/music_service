package org.musicservice.demo.security.verificationToken;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteAllByExpiryDateBefore(Instant expiryDateBefore);

}
