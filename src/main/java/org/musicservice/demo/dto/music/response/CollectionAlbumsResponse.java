package org.musicservice.demo.dto.music.response;

import lombok.Data;

import java.util.List;

@Data
public class CollectionAlbumsResponse {

    private List<AlbumResponse> albums;
}
