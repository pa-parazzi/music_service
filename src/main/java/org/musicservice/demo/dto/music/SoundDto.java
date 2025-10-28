package org.musicservice.demo.dto.music;

import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.image.SoundImageDto;

@Getter
@Setter
public class SoundDto {

    private SoundImageDto soundImage;
    private String title;
    private Integer duration;
    private String key;
    private String url;

}
