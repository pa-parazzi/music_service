package org.musicservice.demo.dto.user;

import lombok.Data;
import org.musicservice.demo.dto.image.ImageResponse;

@Data
public class UserMainResponse {

    private String username;
    private ImageResponse avatar;

}
