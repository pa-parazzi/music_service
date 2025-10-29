package org.musicservice.demo.dto.music;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JamendoResponse {

    private Integer results_count;
    private List<UploadMusicResponse> results;

}
