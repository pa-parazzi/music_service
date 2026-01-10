package org.musicservice.demo.security.verification;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.security.reposiroty.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class VerificationTokenService {

    @Value("${base_url}")
    private String base_url;

    @Value("${expiration_hours}")
    private Duration expirationHours;

    private final VerificationTokenRepository repository;
    private final EmailService emailService;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    public VerificationToken findByToken(String token){
        return repository.findByToken(token).orElse(null);
    }

    @Transactional
    public void createToken(User user){
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(expirationHours);
        VerificationToken newVerificationToken = new VerificationToken(user, token, expiryDate);
        user.setVerificationToken(newVerificationToken);
        repository.save(newVerificationToken);
        String activationLink = base_url + "/api/auth/activate?token=" + token;
        emailService.sendActivationEmail(user.getEmail(), activationLink);
    }

    @Transactional
    public void delete(VerificationToken verificationToken){
        verificationToken.getUser().setVerificationToken(null);
        repository.delete(verificationToken);
    }

    public boolean isExpiryDate(VerificationToken verificationToken){
        return verificationToken.getExpiryDate().isBefore(Instant.now());
    }

}
