package org.musicservice.demo.support.fixture.jpa;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.fixture.integration.AlbumAggregate;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

public class AlbumJpaFixture {

    public static AlbumAggregate albumAggregateWithAlbums(Genre genre, TestEntityManager entityManager, String titleAlbumPrefix){
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        List<Album> albums = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            Album album = entityManager.persist
                    (new Album(titleAlbumPrefix + "_" + i, LocalDate.of(2003,6,10), artist, genre));
            album.setImage(entityManager.persist(MusicFactoryIT.albumImage(album)));
            albums.add(album);
        }
        return new AlbumAggregate(artist, albums);
    }

    public static AlbumAggregate albumAggregateWithOneAlbum(Genre genre, TestEntityManager entityManager){
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        Album album = entityManager.persist(MusicFactoryIT.album(artist, genre));
        album.setImage(entityManager.persist(MusicFactoryIT.albumImage(album)));
        return new AlbumAggregate(artist, List.of(album));
    }
}
