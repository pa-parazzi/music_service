package org.musicservice.demo.exception.user;

import org.musicservice.demo.error.user.UniqueFieldErrorCode;

public class RegistrationException extends RuntimeException{

    private final UniqueFieldErrorCode code;

    public RegistrationException (String message, UniqueFieldErrorCode code){
        super(message);
        this.code = code;
    }

    public UniqueFieldErrorCode getCode(){
        return code;
    }
}
