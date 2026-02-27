package org.musicservice.demo.error.user;

public enum UniqueFieldErrorCode {

    USERNAME("username","USERNAME_ALREADY_EXISTS"), EMAIL("email","EMAIL_ALREADY_EXISTS");

    private final String field;
    private final String errorCode;

    UniqueFieldErrorCode(String field, String errorCode){
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
