package org.musicservice.demo.support.fixture.integration;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

@TestComponent
public class ArtistTestFixture {

    @Autowired
    private ArtistRepository artistRepository;

    public Artist createArtist(Genre genre){
        return artistRepository.save(MusicFactoryIT.artist(genre));
    }

    public void createArtists(Genre genre, String namePrefix){
        for (int i = 0; i < totalElements; i++) {
            artistRepository.save(new Artist(namePrefix + i, genre));
        }
    }
}
