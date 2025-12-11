package org.musicservice.demo.dto.music.request;

import lombok.Data;

@Data
public class LikeRequest {

    private Long userId;
    private String targetType;
    private Long targetId;
}
