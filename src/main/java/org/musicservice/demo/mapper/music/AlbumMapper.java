package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.model.music.Album;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    List<AlbumResponse> toAlbumResponses(List<Album> albums);

    List<Album> toAlbums(List<AlbumResponse> albumResponses);

    AlbumResponse toAlbumResponse(Album album);
}
