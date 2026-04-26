package org.musicservice.demo.dto.music.album;

import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;

import java.time.LocalDate;

public record AlbumResponse(Long id, String title, LocalDate releaseDate, ImageResponse image, ArtistResponse artist) {}
