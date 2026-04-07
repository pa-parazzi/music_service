package org.musicservice.demo.support.assertions;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Sound;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SoundAssertions {

    public static void assertSoundsWithoutRelations(List<Sound> sounds, String titlePrefix, String keyNameEndsWith) {
        assertThat(sounds).extracting(Sound::getId).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(sounds).extracting(Sound::getTitle).allMatch(title -> title.startsWith(titlePrefix));
        assertThat(sounds).extracting(Sound::getDuration).isNotEmpty();
        assertThat(sounds).extracting(Sound::getKey).allMatch(key -> key.endsWith(keyNameEndsWith));
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

    public static void assertSoundsResponse(List<SoundResponse> response, String titlePrefix, String keyNameEndsWith) {
        assertThat(response).extracting(SoundResponse::id).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(response).extracting(SoundResponse::title).allMatch(title -> title.startsWith(titlePrefix));
        assertThat(response).extracting(SoundResponse::duration).isNotEmpty();
        assertThat(response).extracting(SoundResponse::key).allMatch(key -> key.endsWith(keyNameEndsWith));
    }
}
