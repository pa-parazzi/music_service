package org.musicservice.demo.exception.music;

public class LikeNotFoundException extends RuntimeException{

    public LikeNotFoundException (String message){
        super(message);
    }
}
