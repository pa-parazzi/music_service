package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.mapper.image.ImageUrlMapper;

@Mapper(componentModel = "spring", uses = {ImageUrlMapper.class})
public interface AlbumMapper {

    @Mapping(target = "image.url", source = "image.key", qualifiedByName = "mapImgUrl")
    AlbumResponse toAlbumResponse(Album album);
}
