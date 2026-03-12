package org.musicservice.demo.exception.music;

public class GenreDoesNotExistException extends RuntimeException {

    public GenreDoesNotExistException(String message) {
        super(message);
    }
}
