package org.musicservice.demo.dto.music.album;

import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;

public record AlbumResponse(Long id, String title, ImageResponse image, ArtistResponse artist) {}
