package org.musicservice.demo.dto.music.sound;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
public class SoundDto {

    private Long id;
    private String title;
    private Integer duration;
    private String key;
    private String url;

}
