package org.musicservice.demo.exception.response;

public enum RefreshTokenErrorCode {

    MISSING("Refresh token is missing"), INVALID("Refresh token invalid");

    private final String message;

    RefreshTokenErrorCode(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
