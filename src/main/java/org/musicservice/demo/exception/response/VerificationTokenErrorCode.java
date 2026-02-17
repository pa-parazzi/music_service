package org.musicservice.demo.exception.response;

public enum VerificationTokenErrorCode {

    MISSING("ссылка не действительна"), EXPIRED("срок действия ссылки истек");

    private final String message;

    VerificationTokenErrorCode(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
