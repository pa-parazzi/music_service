package org.musicservice.demo.dto.music.response;

import lombok.Data;

@Data
public class LikeResponse {

    private String targetType;
    private Long targetId;
}
