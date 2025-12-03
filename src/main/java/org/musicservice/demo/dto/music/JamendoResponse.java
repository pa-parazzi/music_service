package org.musicservice.demo.dto.music;

import lombok.Data;

import java.util.List;

@Data
public class JamendoResponse {

    private Integer results_count;
    private List<UploadMusicResponse> results;

}
