package org.musicservice.demo.dto.music.album;

import lombok.Data;

import java.util.List;

@Data
public class CollectionAlbumsResponse {

    private List<AlbumResponse> albums;
}
