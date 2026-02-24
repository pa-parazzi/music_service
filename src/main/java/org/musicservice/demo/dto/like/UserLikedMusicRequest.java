package org.musicservice.demo.dto.like;

public record UserLikedMusicRequest(Long userId, Long targetId) {}
