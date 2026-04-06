package org.musicservice.demo.support.assertions;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.musicservice.demo.entity.music.Sound;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SoundAssertions {

    public static void assertSoundsWithOutRelations(List<Sound> sounds, String titlePrefix, String endKeyName) {
        assertThat(sounds).extracting(Sound::getId).isNotEmpty();
        assertThat(sounds).extracting(Sound::getTitle).allMatch(title -> title.startsWith(titlePrefix));
        assertThat(sounds).extracting(Sound::getDuration).isNotEmpty();
        assertThat(sounds).extracting(Sound::getKey).allMatch(key -> key.endsWith(endKeyName));
        assertThat(sounds).extracting(Sound::getReleaseDate).isNotEmpty();

        assertThat(sounds).extracting(Sound::getArtist).allSatisfy(artist -> {
            assertThat(artist).isNotNull().isInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(artist)).isFalse();
        });
        assertThat(sounds).extracting(Sound::getAlbum).allSatisfy(album -> {
            assertThat(album).isNotNull().isInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(album)).isFalse();
        });
        assertThat(sounds).extracting(Sound::getGenre).allSatisfy(genre -> {
            assertThat(genre).isNotNull().isInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(genre)).isFalse();
        });
    }
}
