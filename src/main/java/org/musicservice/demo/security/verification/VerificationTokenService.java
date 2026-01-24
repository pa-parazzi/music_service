package org.musicservice.demo.security.verification;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
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

    @Value("${activation_url}")
    private String activationUrl;

    @Value("${expiration_hours}")
    private Duration expirationHours;

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EntityManager entityManager;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository, EmailService emailService, EntityManager entityManager) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.entityManager = entityManager;
    }

    public VerificationToken findByToken(String token){
        return verificationTokenRepository.findByToken(token).orElse(null);
    }

    @Transactional
    public void createToken(VerifyEmailRequest request){
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(expirationHours);
        User user = entityManager.getReference(User.class, request.userId());
        VerificationToken newVerificationToken = new VerificationToken(user, token, expiryDate);
        verificationTokenRepository.save(newVerificationToken);
        String activationLink = activationUrl + token;
        emailService.sendActivationEmail(request.email(), activationLink);
    }

    @Transactional
    public String verify(String token){
        VerificationToken verificationToken = findByToken(token);
        if(verificationToken==null){
            return "Ваш токен активации истек"; // TODO: Добавить возможность "запросить новый токен"
        } else if(verificationToken.getExpiryDate().isBefore(Instant.now())){
            verificationTokenRepository.delete(verificationToken);
            return "Ваш токен активации истек";
        }
        Long userId = verificationToken.getUser().getId();
        userRepository.enableUser(userId);
        verificationTokenRepository.delete(verificationToken);
        return "Ваш аккаунт активирован!";
    }

}
