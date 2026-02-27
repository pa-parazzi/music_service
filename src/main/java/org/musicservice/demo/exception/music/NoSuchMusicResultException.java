package org.musicservice.demo.exception.music;

public class NoSuchMusicResultException extends RuntimeException {
    public NoSuchMusicResultException(String message) {
        super(message);
    }
}
