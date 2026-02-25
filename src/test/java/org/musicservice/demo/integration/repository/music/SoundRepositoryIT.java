package org.musicservice.demo.integration.repository.music;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
public class SoundRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private SoundRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByIdForCollectionPage_ShouldReturnsCorrectSoundList(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Sound sound2 = entityManager.persistAndFlush(MusicFactoryIT.sound2(artist, album));
        Sound sound = entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        Sound sound3 = entityManager.persistAndFlush(MusicFactoryIT.sound3(artist, album));
        entityManager.clear();
        Long [] orderSoundIds = List.of(sound2.getId(), sound.getId(), sound3.getId()).toArray(Long[]::new);

        List<Sound> result = repository.findAllByIdForCollectionPage(orderSoundIds);
        assertSoundListContainsExactly(result, sound2, sound, sound3);
    }

    @Test
    void findAllByIdForCollectionPage_ShouldReturnsEmptyList_WhenSoundsIdsIsIncorrect(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        entityManager.clear();
        Long [] orderSoundIds = {123L, 234L, 345L};

        List<Sound> result = repository.findAllByIdForCollectionPage(orderSoundIds);
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByArtistId_ShouldReturnsValidSoundList(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Sound sound = entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        Sound sound2 = entityManager.persistAndFlush(MusicFactoryIT.sound2(artist, album));
        Sound sound3 = entityManager.persistAndFlush(MusicFactoryIT.sound3(artist, album));
        entityManager.clear();

        List<Sound> result = repository.findAllByArtistId(artist.getId());
        assertSoundListContainsExactlyInAnyOrder(result, sound, sound2, sound3);
    }

    @Test
    void findAllByArtistId_ShouldReturnsEmptyList_WhenArtistIdIsIncorrect(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        entityManager.clear();

        List<Sound> result = repository.findAllByArtistId(2385L);
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByAlbumId_ShouldReturnsValidSoundList(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Sound sound = entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        Sound sound2 = entityManager.persistAndFlush(MusicFactoryIT.sound2(artist, album));
        Sound sound3 = entityManager.persistAndFlush(MusicFactoryIT.sound3(artist, album));
        entityManager.clear();

        List<Sound> result = repository.findAllByAlbumId(album.getId());
        assertSoundListContainsExactlyInAnyOrder(result, sound, sound2, sound3);
    }

    @Test
    void findAllByAlbumId_ShouldReturnsEmptyList_WhenAlbumIdIsIncorrect(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        entityManager.clear();

        List<Sound> result = repository.findAllByAlbumId(2567L);
        assertThat(result).isEmpty();
    }

    private void assertSoundListContainsExactlyInAnyOrder(List<Sound> result, Sound sound, Sound sound2, Sound sound3){
        assertThat(result)
                .extracting(Sound::getId, Sound::getTitle, Sound::getKey, Sound::getDuration,
                        soundEntity -> soundEntity.getArtist().getId(), soundEntity -> soundEntity.getAlbum().getId())
                .containsExactlyInAnyOrder(
                        tuple(sound.getId(), sound.getTitle(), sound.getKey(), sound.getDuration(), sound.getArtist().getId(), sound.getAlbum().getId()),
                        tuple(sound2.getId(), sound2.getTitle(), sound2.getKey(), sound2.getDuration(), sound2.getArtist().getId(), sound2.getAlbum().getId()),
                        tuple(sound3.getId(), sound3.getTitle(), sound3.getKey(), sound3.getDuration(), sound3.getArtist().getId(), sound3.getAlbum().getId())
                );
    }

    private void assertSoundListContainsExactly(List<Sound> result, Sound sound, Sound sound2, Sound sound3){
        assertThat(result)
                .extracting(Sound::getId, Sound::getTitle, Sound::getKey, Sound::getDuration,
                        soundEntity -> soundEntity.getArtist().getId(), soundEntity -> soundEntity.getAlbum().getId())
                .containsExactly(
                        tuple(sound.getId(), sound.getTitle(), sound.getKey(), sound.getDuration(), sound.getArtist().getId(), sound.getAlbum().getId()),
                        tuple(sound2.getId(), sound2.getTitle(), sound2.getKey(), sound2.getDuration(), sound2.getArtist().getId(), sound2.getAlbum().getId()),
                        tuple(sound3.getId(), sound3.getTitle(), sound3.getKey(), sound3.getDuration(), sound3.getArtist().getId(), sound3.getAlbum().getId())
                );
    }


}
