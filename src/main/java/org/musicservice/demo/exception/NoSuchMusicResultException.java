package org.musicservice.demo.exception;

public class NoSuchMusicResultException extends RuntimeException {
    public NoSuchMusicResultException(String message) {
        super(message);
    }
}
