package org.musicservice.demo.exception.refreshTokenError;

public class RefreshTokenNotFoundException extends RuntimeException{

    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
