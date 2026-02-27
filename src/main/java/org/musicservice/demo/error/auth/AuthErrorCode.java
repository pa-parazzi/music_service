package org.musicservice.demo.error.auth;

public enum AuthErrorCode {

    BAD_CREDENTIALS("Неверный логин или пароль"),
    ACCOUNT_LOCKED("Превышено максимальное число попыток входа. Попробуйте позже."),
    ACCOUNT_NOT_ACTIVATED("Учетная запись не активирована. Проверьте почту."),
    BAD_AUTHENTICATION_REQUEST("Ошибка аутентификации");

    private final String message;

    AuthErrorCode(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
