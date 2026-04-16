package org.musicservice.demo.integration.repository.music;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.support.config.AbstractJpaIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.assertions.SoundAssertions.assertSoundsWithoutRelations;
import static org.musicservice.demo.support.fixture.jpa.SoundJpaFixture.soundAggregateWithOneSound;
import static org.musicservice.demo.support.fixture.jpa.SoundJpaFixture.soundAggregateWithSounds;

public class SoundRepositoryIT extends AbstractJpaIT {

    @Autowired
    private SoundRepository repository;
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre findGenre(){
        return genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void findByIdForSoundPage_ShouldReturnSoundWithCorrectlyRelations(){
        Genre genre = findGenre();
        Sound expectedSound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();

        entityManager.flush();
        entityManager.clear();

        Sound actualSound = repository.findByIdForSoundPage(expectedSound.getId()).orElseThrow();

        assertThat(actualSound.getId()).isEqualTo(expectedSound.getId());
        assertThat(actualSound.getTitle()).isEqualTo(expectedSound.getTitle());
        assertThat(actualSound.getDuration()).isEqualTo(expectedSound.getDuration());
        assertThat(actualSound.getReleaseDate()).isEqualTo(expectedSound.getReleaseDate());
        assertThat(actualSound.getKey()).isEqualTo(expectedSound.getKey());

        assertThat(actualSound.getArtist()).isNotNull().isNotInstanceOf(HibernateProxy.class);
        assertThat(Hibernate.isInitialized(actualSound.getArtist())).isTrue();

        assertThat(actualSound.getAlbum()).isNotNull().isNotInstanceOf(HibernateProxy.class);
        assertThat(Hibernate.isInitialized(actualSound.getAlbum())).isTrue();

        assertThat(actualSound.getAlbum().getImage()).isNotNull().isNotInstanceOf(HibernateProxy.class);
        assertThat(Hibernate.isInitialized(actualSound.getAlbum().getImage())).isTrue();
    }

    @Test
    void findByIdForSoundPage_ShouldReturnEmpty_WhenIdIsInvalid(){
        assertThat(repository.findByIdForSoundPage(89283L)).isEmpty();
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsFirstPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName);

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByTitleStartingWithIgnoreCase(soundTitlePrefix, PageRequest.of(page, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertFirstPage(soundPage);
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsSecondPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName);

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByTitleStartingWithIgnoreCase(soundTitlePrefix, PageRequest.of(page + 1, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertSecondPage(soundPage);
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsLastPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName);

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByTitleStartingWithIgnoreCase(soundTitlePrefix, PageRequest.of(page + 2, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertLastPage(soundPage);
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsEmpty_WhenSoundNotFoundByPrefix(){
        Page<Sound> soundPage = repository
                .findByTitleStartingWithIgnoreCase("incorrect sound prefix", PageRequest.of(page, size));

        assertEmptyPage(soundPage);
    }

    @Test
    void findByArtistId_ShouldReturnsFirstPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Artist artist = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).artist();

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByArtistId(artist.getId(), PageRequest.of(page, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertFirstPage(soundPage);
    }

    @Test
    void findByArtistId_ShouldReturnsSecondPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Artist artist = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).artist();

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByArtistId(artist.getId(), PageRequest.of(page + 1, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertSecondPage(soundPage);
    }

    @Test
    void findByArtistId_ShouldReturnsLastPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Artist artist = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).artist();

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByArtistId(artist.getId(), PageRequest.of(page + 2, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertLastPage(soundPage);
    }

    @Test
    void findByArtistId_ShouldReturnsEmpty_WhenArtistIdIsInvalid(){
        Page<Sound> soundPage = repository
                .findByArtistId(23616L, PageRequest.of(page, size));

        assertEmptyPage(soundPage);
    }

    @Test
    void findByAlbumId_ShouldReturnsFirstPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Album album = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).album();

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByAlbumId(album.getId(), PageRequest.of(page, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertFirstPage(soundPage);
    }

    @Test
    void findByAlbumId_ShouldReturnsSecondPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Album album = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).album();

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByAlbumId(album.getId(), PageRequest.of(page + 1, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertSecondPage(soundPage);
    }

    @Test
    void findByAlbumId_ShouldReturnsLastPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Album album = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).album();

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByAlbumId(album.getId(), PageRequest.of(page + 2, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertLastPage(soundPage);
    }

    @Test
    void findByAlbumId_ShouldReturnsEmpty_WhenAlbumIdIsInvalid(){
        Page<Sound> soundPage = repository
                .findByAlbumId(79129L, PageRequest.of(page, size));

        assertEmptyPage(soundPage);
    }

    @Test
    void findByGenreId_ShouldReturnsFirstPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName);

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByGenreId(genre.getId(), PageRequest.of(page, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertFirstPage(soundPage);
    }

    @Test
    void findByGenreId_ShouldReturnsSecondPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName);

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByGenreId(genre.getId(), PageRequest.of(page + 1, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertSecondPage(soundPage);
    }

    @Test
    void findByGenreId_ShouldReturnsLastPageCorrectly(){
        Genre genre = findGenre();
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName);

        entityManager.flush();
        entityManager.clear();

        Page<Sound> soundPage = repository
                .findByGenreId(genre.getId(), PageRequest.of(page + 2, size));
        List<Sound> sounds = soundPage.getContent();

        assertSoundsWithoutRelations(sounds, soundTitlePrefix, endKeyName);
        assertLastPage(soundPage);
    }

    @Test
    void findByGenreId_ShouldReturnsEmpty_WhenGenreIdIsInvalid(){
        Page<Sound> soundPage = repository
                .findByGenreId(8341L, PageRequest.of(page, size));

        assertEmptyPage(soundPage);
    }

}
