package org.musicservice.demo.security.verificationToken;

public interface MailService {
    void sendActivationEmail(String email, String activationLink);
}
