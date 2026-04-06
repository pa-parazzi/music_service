package org.musicservice.demo.dto.music.album;

import org.musicservice.demo.dto.image.ImageResponse;

public record AlbumInfo(Long id, String title, ImageResponse image) {}
