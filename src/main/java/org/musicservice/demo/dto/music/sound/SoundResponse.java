package org.musicservice.demo.dto.music.sound;

public record SoundResponse(Long id, String title, Integer duration, String key, String url) {}