package org.musicservice.demo.dto.jamendo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JamendoResponse {
    private List<UploadMusicResponse> results;
}
