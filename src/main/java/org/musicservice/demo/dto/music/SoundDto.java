package org.musicservice.demo.dto.music;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoundDto {

    private String title;
    private Integer duration;
    private String key;
    private String url;

}
