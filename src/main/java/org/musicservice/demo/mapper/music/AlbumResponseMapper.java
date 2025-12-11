package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.response.AlbumResponse;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.model.music.Album;

@Mapper(componentModel = "spring", uses = {AlbumImageMapper.class, ArtistMapper.class, SoundMapper.class})
public interface AlbumResponseMapper {

    @Mapping(target = "albumId", source = "id")
    @Mapping(target = "albumImage", source = "image")
    AlbumResponse toAlbumResponse(Album album);
}
