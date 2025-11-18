package org.musicservice.demo.factory;

import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@Component
public class ArtistFactory {

    public Artist createFactory(){
        return new Artist("Muse");
    }
}
