package org.musicservice.demo.factory.music;

import org.musicservice.demo.model.music.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumFactory {

    public Album createFactoryAlbum(){
        return new Album("Black Holes and Revelations");
    }
}
