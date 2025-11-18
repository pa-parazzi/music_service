package org.musicservice.demo.factory;

import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;

@Component
public class AlbumFactory {

    public Album createFactoryAlbum(){
        return new Album("Black Holes and Revelations");
    }
}
