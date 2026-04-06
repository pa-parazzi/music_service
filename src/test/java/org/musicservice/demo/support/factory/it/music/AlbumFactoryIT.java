package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

public class AlbumFactoryIT {

    public static List<Album> prepareAlbumsWithAllRelations(TestEntityManager entityManager,
                                                            Genre genre, Artist artist,
                                                            String titleAlbumPrefix){
        List<Album> albums = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            Album album = entityManager.persist
                    (new Album(titleAlbumPrefix + "_" + i, LocalDate.of(2003,6,10), artist, genre));
            album.setImage(entityManager.persist(MusicFactoryIT.albumImage(album)));
            albums.add(album);
        }
        return albums;
    }

    public static Album prepareAlbumWithAllRelations(TestEntityManager entityManager){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        Album album = entityManager.persist(MusicFactoryIT.album(artist, genre));
        album.setImage(entityManager.persist(MusicFactoryIT.albumImage(album)));
        return album;
    }
}
