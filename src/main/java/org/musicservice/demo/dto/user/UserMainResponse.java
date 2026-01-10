package org.musicservice.demo.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.image.AvatarDto;

@Data
public class UserMainResponse {

    private Long id;
    private String username;
    private AvatarDto avatar;

}
