package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.model.music.Album;

@Mapper(componentModel = "spring", uses = {AlbumImageMapper.class})
public interface AlbumMapper {

    @Mapping(target = "albumId", source = "id")
    @Mapping(target = "albumImage", source = "image")
    @Mapping(target = "artist", source = "artist")
    AlbumResponse toAlbumResponse(Album album);
}
