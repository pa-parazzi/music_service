package org.musicservice.demo.support.assertions;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.musicservice.demo.entity.likes.AlbumLike;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumLikeAssertions {

    public static void assertAlbumLikeWithAlbum(List<AlbumLike> albumLikes){
        assertThat(albumLikes).extracting(AlbumLike::getAlbum).allSatisfy(album -> {
            assertThat(album).isNotNull().isNotInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(album)).isTrue();
        });
    }
}
