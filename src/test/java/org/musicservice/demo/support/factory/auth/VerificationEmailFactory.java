package org.musicservice.demo.support.factory.auth;

import org.musicservice.demo.security.dto.VerifyEmailRequest;

public class VerificationEmailFactory {

    public static VerifyEmailRequest verifyEmailRequest(){
        return new VerifyEmailRequest(1L, "test@mail.com");
    }
}
