package org.musicservice.demo.support.fixture.jpa;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

public class ArtistJpaFixture {

    public static void createArtists(Genre genre, TestEntityManager entityManager, String artistNamePrefix){
        for (int i = 0; i < totalElements; i++) {
            entityManager.persist(new Artist(artistNamePrefix + "_" + i, genre));
        }
    }
}
