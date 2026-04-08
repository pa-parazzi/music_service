package org.musicservice.demo.support.fixture;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;

import java.util.List;

public record SoundAggregate(Genre genre, Artist artist, Album album, List<Sound> sounds) {}