package org.musicservice.demo.dto.music.response;

import lombok.Data;

import java.util.List;

@Data
public class MainResponse {
    private List<AlbumResponse> albums;
}
