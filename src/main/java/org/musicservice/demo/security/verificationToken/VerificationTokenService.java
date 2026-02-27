package org.musicservice.demo.security.verificationToken;

import org.musicservice.demo.dto.user.ResponseToEmailVerification;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.auth.VerifyEmailTokenException;
import org.musicservice.demo.error.auth.VerificationTokenErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
import org.musicservice.demo.security.properties.VerificationTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenProperties properties;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository, VerificationTokenProperties properties, UserRepository userRepository, MailService mailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.properties = properties;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    public VerificationToken findByToken(String token){
        return verificationTokenRepository.findByToken(token).orElseThrow(() -> new VerifyEmailTokenException(VerificationTokenErrorCode.MISSING));
    }

    @Transactional
    public void createToken(VerifyEmailRequest request){
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(properties.getExpirationHours());
        User userProxy = userRepository.getReferenceById(request.userId());
        verificationTokenRepository.save(new VerificationToken(userProxy, token, expiryDate));
        String activationLink = properties.getActivationUrl() + token;
        mailService.sendActivationEmail(request.email(), activationLink);
    }

    @Transactional
    public ResponseToEmailVerification verify(String token){
        if(token==null) throw new VerifyEmailTokenException(VerificationTokenErrorCode.MISSING);
        VerificationToken verificationToken = findByToken(token);
        if(isExpired(verificationToken)){
            verificationTokenRepository.delete(verificationToken);
            throw new VerifyEmailTokenException(VerificationTokenErrorCode.EXPIRED);
        }
        Long userId = verificationToken.getUser().getId();
        userRepository.enableUser(userId);
        verificationTokenRepository.delete(verificationToken);
        return new ResponseToEmailVerification("Ваш аккаунт активирован!");
    }

    private boolean isExpired(VerificationToken verificationToken){
        return verificationToken.getExpiryDate().isBefore(Instant.now());
    }
}
