package org.musicservice.demo.dto.user;

import org.musicservice.demo.dto.image.ImageResponse;

public record UserMainResponse(String username, ImageResponse avatar) {}