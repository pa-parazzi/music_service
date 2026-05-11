package org.musicservice.demo.dto.music.sound;

import org.musicservice.demo.dto.music.album.AlbumInfo;
import org.musicservice.demo.dto.music.artist.ArtistResponse;

import java.time.LocalDate;

public record SoundPageResponse(
        Long id, String title, Integer duration, String key, String url,
        LocalDate releaseDate, ArtistResponse artist, AlbumInfo album) {}