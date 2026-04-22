package org.musicservice.demo.integration.repository.likes;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractJpaIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.assertions.SoundAssertions.assertSoundsWithoutRelations;
import static org.musicservice.demo.support.assertions.SoundLikeAssertions.assertSoundLikesWithSounds;
import static org.musicservice.demo.support.fixture.jpa.SoundJpaFixture.soundAggregateWithOneSound;
import static org.musicservice.demo.support.fixture.jpa.SoundJpaFixture.soundAggregateWithSounds;
import static org.musicservice.demo.support.fixture.jpa.SoundLikeJpaFixture.createSoundLikes;

public class SoundLikeRepositoryIT extends AbstractJpaIT {

    @Autowired
    private SoundLikeRepository repository;
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre findGenre(){
        return genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void deleteByUserIdAndSoundId_ShouldDeleteRecord() {
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();
        createSoundLikes(entityManager, user, List.of(sound));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(user.getId(), sound.getId());
        entityManager.flush();
        entityManager.clear();

        List<SoundLike> soundLikes = repository.findAll();
        assertThat(soundLikes).isEmpty();
    }

    @Test
    void deleteByUserIdAndSoundId_ShouldDoNothing_WhenUserIdIsIncorrectly() {
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();
        createSoundLikes(entityManager, user, List.of(sound));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(9810L, sound.getId());
        entityManager.flush();
        entityManager.clear();

        List<SoundLike> soundLikes = repository.findAll();
        assertThat(soundLikes).isNotEmpty();
    }

    @Test
    void deleteByUserIdAndSoundId_ShouldDoNothing_WhenSoundIdIsIncorrectly() {
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();
        createSoundLikes(entityManager, user, List.of(sound));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(user.getId(), 6512L);
        entityManager.flush();
        entityManager.clear();

        List<SoundLike> soundLikes = repository.findAll();
        assertThat(soundLikes).isNotEmpty();
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsFirstPageCorrectly() {
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        String soundTitlePrefix = "bad romance";
        String endKeyName = "key";
        List<Sound> sounds = soundAggregateWithSounds(genre, entityManager,soundTitlePrefix, endKeyName).sounds();
        createSoundLikes(entityManager, user, sounds);

        entityManager.flush();
        entityManager.clear();

        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();

        assertSoundLikesWithSounds(soundLikes);

        List<Sound> soundsBySoundLikes = soundLikes.stream().map(SoundLike::getSound).toList();

        assertFirstPage(soundLikePage);
        assertSoundLikesOrderByCreatedAtDesc(soundLikes);
        assertSoundsWithoutRelations(soundsBySoundLikes, soundTitlePrefix, endKeyName);
    }



    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsSecondPageCorrectly() {
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        String soundTitlePrefix = "starlight";
        String endKeyName = "key";
        List<Sound> sounds = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).sounds();
        createSoundLikes(entityManager, user, sounds);

        entityManager.flush();
        entityManager.clear();

        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 1, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();

        assertSoundLikesWithSounds(soundLikes);

        List<Sound> soundsBySoundLikes = soundLikes.stream().map(SoundLike::getSound).toList();

        assertSecondPage(soundLikePage);
        assertSoundLikesOrderByCreatedAtDesc(soundLikes);
        assertSoundsWithoutRelations(soundsBySoundLikes, soundTitlePrefix, endKeyName);
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsLastPageCorrectly() {
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        String soundTitlePrefix = "poker face";
        String endKeyName = "key";
        List<Sound> sounds = soundAggregateWithSounds(genre, entityManager, soundTitlePrefix, endKeyName).sounds();
        createSoundLikes(entityManager, user, sounds);

        entityManager.flush();
        entityManager.clear();

        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 2, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();

        assertSoundLikesWithSounds(soundLikes);

        List<Sound> soundsBySoundLikes = soundLikes.stream().map(SoundLike::getSound).toList();

        assertLastPage(soundLikePage);
        assertSoundLikesOrderByCreatedAtDesc(soundLikes);
        assertSoundsWithoutRelations(soundsBySoundLikes, soundTitlePrefix, endKeyName);
    }

    @Test
    void findByUserIdOrderByCreatedAtDescIdDesc_ShouldReturnsEmptyPage_WhenUserIdIsInvalid() {
        Page<SoundLike> soundLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(8491L, PageRequest.of(page, size));
        assertEmptyPage(soundLikePage);
    }

    @Test
    void existsByUserIdAndSoundId_ShouldReturnIsTrue_WhenSoundLikeIsExists(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();
        createSoundLikes(entityManager, user, List.of(sound));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndSoundId(user.getId(), sound.getId());
        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndSoundId_ShouldReturnFalse_WhenAlbumIdIsInvalid(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();
        createSoundLikes(entityManager, user, List.of(sound));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndSoundId(user.getId(), 8902L);
        assertThat(result).isFalse();
    }

    @Test
    void existsByUserIdAndSoundId_ShouldReturnFalse_WhenUserIdIsInvalid(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Sound sound = soundAggregateWithOneSound(genre, entityManager).sounds().getFirst();
        createSoundLikes(entityManager, user, List.of(sound));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndSoundId(8919L, sound.getId());
        assertThat(result).isFalse();
    }

    private void assertSoundLikesOrderByCreatedAtDesc(List<SoundLike> soundLikes) {
        List<Instant> createdAtSoundLikesOrder = soundLikes.stream().map(SoundLike::getCreatedAt).toList();
        assertThat(createdAtSoundLikesOrder).isSortedAccordingTo(Comparator.reverseOrder());
    }
}