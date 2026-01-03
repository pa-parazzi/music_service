package org.musicservice.demo.dto.music.album;

import lombok.Data;

import java.util.List;

@Data
public class MainAlbumResponse {
    private List<AlbumResponse> albums;
}
