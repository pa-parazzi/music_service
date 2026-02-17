package org.musicservice.demo.exception;

import org.musicservice.demo.exception.response.VerificationTokenErrorCode;

public class VerifyEmailTokenException extends RuntimeException {

    private final VerificationTokenErrorCode errorCode;

    public VerifyEmailTokenException(VerificationTokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public VerificationTokenErrorCode getErrorCode(){
        return this.errorCode;
    }
}
