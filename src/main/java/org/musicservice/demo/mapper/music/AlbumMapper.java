package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.AlbumDto;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.model.music.Album;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    Album convertToAlbum(AlbumDto albumDto);

    AlbumDto convertToDto(Album album);

    AlbumResponse convertToAlbumResponse(Album album);
}
