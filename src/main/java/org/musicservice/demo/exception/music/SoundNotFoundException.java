package org.musicservice.demo.exception.music;

public class SoundNotFoundException extends RuntimeException{

    public SoundNotFoundException(String message){
        super(message);
    }
}
