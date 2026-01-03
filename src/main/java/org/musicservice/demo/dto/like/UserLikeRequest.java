package org.musicservice.demo.dto.like;

import lombok.Data;

@Data
public class UserLikeRequest {
    private Long userId;
    private Long targetId;
}
