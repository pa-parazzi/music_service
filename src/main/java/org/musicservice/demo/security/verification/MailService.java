package org.musicservice.demo.security.verification;

public interface MailService {
    void sendActivationEmail(String email, String activationLink);
}
