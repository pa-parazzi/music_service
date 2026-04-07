package org.musicservice.demo.support.assertions;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.entity.music.Album;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumAssertions {

    public static void assertAlbumsWithArtistAndImage(List<Album> albums, String titlePrefix){
        assertThat(albums).extracting(Album::getId).allMatch(Objects::nonNull).doesNotHaveDuplicates();
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

    public static void assertAlbumsResponse(List<AlbumResponse> response,
                                            String titlePrefix, String imgKeyNameEndsWith){
        assertThat(response).extracting(AlbumResponse::id).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(response).extracting(AlbumResponse::title).allMatch(title -> title.startsWith(titlePrefix));

        assertThat(response).extracting(AlbumResponse::image).allSatisfy(image -> {
            assertThat(image.key().endsWith(imgKeyNameEndsWith)).isTrue();
            assertThat(image.url()).isNotBlank();
        });
        assertThat(response).extracting(AlbumResponse::artist).allSatisfy(artist -> {
            assertThat(artist.id()).isNotNull();
            assertThat(artist.name()).isNotBlank();
        });
    }
}
