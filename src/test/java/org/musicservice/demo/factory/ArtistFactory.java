package org.musicservice.demo.factory;

import org.musicservice.demo.model.music.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistFactory {

    public Artist createFactory(){
        return new Artist("Muse");
    }
}
