package org.musicservice.demo.integration.jamendo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JamendoResponse {
    private List<MusicResponse> results;
}
