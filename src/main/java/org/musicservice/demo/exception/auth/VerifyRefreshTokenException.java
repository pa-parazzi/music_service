package org.musicservice.demo.exception.auth;

import org.musicservice.demo.error.auth.RefreshTokenErrorCode;

public class VerifyRefreshTokenException extends RuntimeException {

    private final RefreshTokenErrorCode code;

    public VerifyRefreshTokenException(RefreshTokenErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public RefreshTokenErrorCode getCode(){
        return code;
    }
}
