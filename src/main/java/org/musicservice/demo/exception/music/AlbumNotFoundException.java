package org.musicservice.demo.exception.music;

public class AlbumNotFoundException extends RuntimeException{

    public AlbumNotFoundException (String message){
        super(message);
    }
}
