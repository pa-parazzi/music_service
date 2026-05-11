package org.musicservice.demo.dto.music.sound;

public record SoundResponse(Long id, Long albumId, String title, Integer duration, String key, String url) {}