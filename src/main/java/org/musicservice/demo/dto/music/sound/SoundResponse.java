package org.musicservice.demo.dto.music.sound;

import lombok.Data;

@Data
public class SoundResponse {

    private Long id;
    private String title;
    private Integer duration;
    private String key;
    private String url;

}
