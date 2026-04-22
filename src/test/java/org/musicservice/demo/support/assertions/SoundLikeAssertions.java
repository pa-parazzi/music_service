package org.musicservice.demo.support.assertions;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.musicservice.demo.entity.likes.SoundLike;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SoundLikeAssertions {

    public static void assertSoundLikesWithSounds(List<SoundLike> soundLikes){
        assertThat(soundLikes).extracting(SoundLike::getSound).allSatisfy(sound -> {
            assertThat(sound).isNotNull().isNotInstanceOf(HibernateProxy.class);
            assertThat(Hibernate.isInitialized(sound)).isTrue();
        });
    }
}
