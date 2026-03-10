package org.musicservice.demo.dto.metadata;

public record TrackMetadata(
        String name, String album_name,
        String album_image, String audiodownload,
        String mp3Key, String albumImgKey) {}