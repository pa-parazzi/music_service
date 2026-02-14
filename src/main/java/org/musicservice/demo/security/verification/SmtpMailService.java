package org.musicservice.demo.security.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailService implements MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public SmtpMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendActivationEmail(String email, String activationLink){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Активация аккаунта");
        message.setText("Для активации перейдите по ссылке: " + activationLink);
        javaMailSender.send(message);
    }
}
