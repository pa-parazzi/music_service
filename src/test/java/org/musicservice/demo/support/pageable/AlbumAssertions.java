package org.musicservice.demo.support.pageable;

import org.hibernate.Hibernate;
import org.musicservice.demo.entity.music.Album;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumAssertions {

    public static void assertAlbumsWithArtistAndImage(List<Album> albums, String titlePrefix){
        assertThat(albums).extracting(Album::getTitle).allMatch(title -> title.startsWith(titlePrefix));
        assertThat(albums).extracting(Album::getArtist).allMatch(Hibernate::isInitialized);
        assertThat(albums).extracting(Album::getImage).allMatch(Hibernate::isInitialized);
    }
}
