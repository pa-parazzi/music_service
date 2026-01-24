package org.musicservice.demo.exception;

import org.musicservice.demo.exception.response.UniqueFieldErrorCode;

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
