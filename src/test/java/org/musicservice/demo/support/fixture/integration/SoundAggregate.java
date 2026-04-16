package org.musicservice.demo.support.fixture.integration;

import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;

import java.util.List;

public record SoundAggregate(Artist artist, Album album, List<Sound> sounds) {}