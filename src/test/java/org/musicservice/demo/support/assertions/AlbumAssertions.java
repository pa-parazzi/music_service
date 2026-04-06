package org.musicservice.demo.support.assertions;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.musicservice.demo.entity.music.Album;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumAssertions {

    public static void assertAlbumsWithArtistAndImage(List<Album> albums, String titlePrefix){
        assertThat(albums).extracting(Album::getId).isNotEmpty();
        assertThat(albums).extracting(Album::getTitle).allMatch(title -> title.startsWith(titlePrefix));
        assertThat(albums).extracting(Album::getReleaseDate).isNotEmpty();

        assertThat(albums).extracting(Album::getArtist).allSatisfy(artist -> {
            assertThat(artist).isNotNull().isNotInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(artist)).isTrue();
        });
        assertThat(albums).extracting(Album::getImage).allSatisfy(image -> {
            assertThat(image).isNotNull().isNotInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(image)).isTrue();
        });
    }
}
