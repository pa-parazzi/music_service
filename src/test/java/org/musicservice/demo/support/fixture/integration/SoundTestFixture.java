package org.musicservice.demo.support.fixture.integration;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

@TestComponent
public class SoundTestFixture {

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumImageRepository albumImageRepository;
    @Autowired
    private SoundRepository soundRepository;

    public SoundAggregate soundAggregateWithOneSound(Genre genre){
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));
        Sound sound = soundRepository.save(MusicFactoryIT.sound(artist, album, genre));
        return new SoundAggregate(artist, album, List.of(sound));
    }

    public SoundAggregate soundAggregateWithSounds(Genre genre, String titlePrefix, String keyNameEndsWith) {
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));
        List<Sound> sounds = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            sounds.add(soundRepository.save(new Sound(titlePrefix + "_" + i, 260, artist, album,
                    i + "_" + keyNameEndsWith, LocalDate.of(2018, 5, 19), genre)));
        }
        return new SoundAggregate(artist, album, sounds);
    }
}
