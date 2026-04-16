package org.musicservice.demo.exception.music;

public class NoSuchMusicException extends RuntimeException {
    public NoSuchMusicException(String message) {
        super(message);
    }
}
