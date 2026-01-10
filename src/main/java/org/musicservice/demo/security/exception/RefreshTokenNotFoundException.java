package org.musicservice.demo.security.exception;

public class RefreshTokenNotFoundException extends RuntimeException{

    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
