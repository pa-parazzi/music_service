package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

public class SoundFactoryIT {

    public static List<Sound> prepareSounds(TestEntityManager entityManager, Genre genre,
                                            Artist artist, Album album,
                                            String soundTitlePrefix, String endKeyName) {
        List<Sound> sounds = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            sounds.add(entityManager.persist
                    (new Sound(soundTitlePrefix + "_" + i,
                            265, artist, album, "sound_" + i + endKeyName,
                            LocalDate.of(2012, 6, 19), genre)));
        }
        return sounds;
    }

    public static Sound prepareSoundWithAllRelations(TestEntityManager entityManager){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        Album album = entityManager.persist(MusicFactoryIT.album(artist, genre));
        return entityManager.persist(MusicFactoryIT.sound(artist, album, genre));
    }
}
