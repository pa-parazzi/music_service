package org.musicservice.demo.service.security;

import org.musicservice.demo.model.user.User;
import org.musicservice.demo.model.user.VerificationToken;
import org.musicservice.demo.repository.user.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
        LocalDateTime expiryDate = LocalDateTime.now().plus(expirationHours);
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
        return verificationToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

}
