package org.musicservice.demo.support.factory.unit.auth;

import org.musicservice.demo.security.dto.VerifyEmailRequest;

public class VerificationTokenFactory {

    public static VerifyEmailRequest verifyEmailRequest(){
        return new VerifyEmailRequest(1L, "test@mail.com");
    }
}
