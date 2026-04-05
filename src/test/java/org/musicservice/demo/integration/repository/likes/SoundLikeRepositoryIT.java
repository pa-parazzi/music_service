package org.musicservice.demo.integration.repository.likes;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.pageable.PageAssertions.*;

@DataJpaTest
public class SoundLikeRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private SoundLikeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteByUserIdAndSoundId_ShouldDeleteRecord() {
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = prepareSound();
        SoundLike soundLike = entityManager.persist(MusicFactoryIT.soundLike(user, sound));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(user.getId(), sound.getId());
        entityManager.flush();
        entityManager.clear();

        SoundLike foundEntity = entityManager.find(SoundLike.class, soundLike.getId());
        assertThat(foundEntity).isNull();
    }

    @Test
    void deleteByUserIdAndSoundId_ShouldDoNothing_WhenUserIdIsIncorrectly() {
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = prepareSound();
        SoundLike soundLike = entityManager.persist(MusicFactoryIT.soundLike(user, sound));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(9810L, sound.getId());
        entityManager.flush();
        entityManager.clear();

        SoundLike foundEntity = entityManager.find(SoundLike.class, soundLike.getId());
        assertThat(foundEntity).isNotNull();
    }

    @Test
    void deleteByUserIdAndSoundId_ShouldDoNothing_WhenSoundIdIsIncorrectly() {
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = prepareSound();
        SoundLike soundLike = entityManager.persist(MusicFactoryIT.soundLike(user, sound));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(user.getId(), 6512L);
        entityManager.flush();
        entityManager.clear();

        SoundLike foundEntity = entityManager.find(SoundLike.class, soundLike.getId());
        assertThat(foundEntity).isNotNull();
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsFirstPageCorrectly() {
        User user = entityManager.persist(UserDataFactoryIT.user());
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        List<Sound> sounds = prepareSounds(genre, soundTitlePrefix, endKeyName);
        prepareSoundLikes(user, sounds);

        entityManager.flush();
        entityManager.clear();

        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();
        List<Sound> soundsBySoundLikes = soundLikes.stream().map(SoundLike::getSound).toList();

        assertFirstPage(soundLikePage);
        assertSoundLikesOrderByCreatedAtDesc(soundLikes);
        assertSounds(soundsBySoundLikes, genre.getName(), soundTitlePrefix, endKeyName);
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsSecondPageCorrectly() {
        User user = entityManager.persist(UserDataFactoryIT.user());
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        List<Sound> sounds = prepareSounds(genre, soundTitlePrefix, endKeyName);
        prepareSoundLikes(user, sounds);

        entityManager.flush();
        entityManager.clear();

        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 1, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();
        List<Sound> soundsBySoundLikes = soundLikes.stream().map(SoundLike::getSound).toList();

        assertSecondPage(soundLikePage);
        assertSoundLikesOrderByCreatedAtDesc(soundLikes);
        assertSounds(soundsBySoundLikes, genre.getName(), soundTitlePrefix, endKeyName);
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsLastPageCorrectly() {
        User user = entityManager.persist(UserDataFactoryIT.user());
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        List<Sound> sounds = prepareSounds(genre, soundTitlePrefix, endKeyName);
        prepareSoundLikes(user, sounds);

        entityManager.flush();
        entityManager.clear();

        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 2, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();
        List<Sound> soundsBySoundLikes = soundLikes.stream().map(SoundLike::getSound).toList();

        assertLastPage(soundLikePage);
        assertSoundLikesOrderByCreatedAtDesc(soundLikes);
        assertSounds(soundsBySoundLikes, genre.getName(), soundTitlePrefix, endKeyName);
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsEmptyPage_WhenUserIdIsInvalid() {
        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(8491L, PageRequest.of(page + 2, size));
        assertEmptyPage(soundLikePage);
    }

    @Test
    void existsByUserIdAndSoundId_ShouldReturnIsTrue_WhenSoundLikeIsExists(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = prepareSound();
        entityManager.persist(MusicFactoryIT.soundLike(user, sound));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndSoundId(user.getId(), sound.getId());
        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndSoundId_ShouldReturnFalse_WhenAlbumIdIsInvalid(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = prepareSound();
        entityManager.persist(MusicFactoryIT.soundLike(user, sound));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndSoundId(user.getId(), 8902L);
        assertThat(result).isFalse();
    }

    @Test
    void existsByUserIdAndSoundId_ShouldReturnFalse_WhenUserIdIsInvalid(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = prepareSound();
        entityManager.persist(MusicFactoryIT.soundLike(user, sound));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndSoundId(8919L, sound.getId());
        assertThat(result).isFalse();
    }

    private void assertSoundLikesOrderByCreatedAtDesc(List<SoundLike> soundLikes) {
        List<Instant> createdAtSoundLikesOrder = soundLikes.stream().map(SoundLike::getCreatedAt).toList();
        assertThat(createdAtSoundLikesOrder).isSortedAccordingTo(Comparator.reverseOrder());
    }

    private void assertSounds(List<Sound> sounds, GenreName genreName, String titlePrefix, String endKeyName) {
        assertThat(sounds).extracting(Sound::getTitle).allMatch(title -> title.startsWith(titlePrefix));
        assertThat(sounds).extracting(Sound::getDuration).isNotEmpty();
        assertThat(sounds).extracting(Sound::getKey).allMatch(key -> key.endsWith(endKeyName));
        assertThat(sounds).extracting(Sound::getArtist).allMatch(artist -> !Hibernate.isInitialized(artist));
        assertThat(sounds).extracting(Sound::getAlbum).allMatch(artist -> !Hibernate.isInitialized(artist));
        assertThat(sounds).extracting(Sound::getReleaseDate).isNotEmpty();
        assertThat(sounds).extracting(sound -> sound.getGenre().getName()).allMatch(gName -> gName.equals(genreName));
    }

    private List<Sound> prepareSounds(Genre genre, String soundTitlePrefix, String endKeyName) {
        List<Sound> sounds = new ArrayList<>();
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        Album album = entityManager.persist(MusicFactoryIT.album(artist, genre));
        for (int i = 0; i < totalElements; i++) {
            sounds.add(entityManager.persist
                    (new Sound(soundTitlePrefix + "_" + i, 265, artist, album,
                            "sound_" + i + endKeyName, LocalDate.of(2012, 6, 19), genre)));
        }
        return sounds;
    }

    private void prepareSoundLikes(User user, List<Sound> sounds) {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        int second = 1;
        for (Sound sound : sounds) {
            SoundLike soundLike = entityManager.persist(MusicFactoryIT.soundLike(user, sound));
            soundLike.setCreatedAt(createdAt.plusSeconds(second++));
        }
    }

    private Sound prepareSound(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        Album album = entityManager.persist(MusicFactoryIT.album(artist, genre));
        return entityManager.persist(MusicFactoryIT.sound(artist, album, genre));
    }
}