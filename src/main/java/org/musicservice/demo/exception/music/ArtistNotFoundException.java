package org.musicservice.demo.exception.music;

public class ArtistNotFoundException extends RuntimeException{

    public ArtistNotFoundException (String message){
        super(message);
    }
}
