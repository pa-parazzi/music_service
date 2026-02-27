package org.musicservice.demo.exception.music;

public class MusicNotFoundException extends RuntimeException {

    public MusicNotFoundException(String message) {
        super(message);
    }

}
