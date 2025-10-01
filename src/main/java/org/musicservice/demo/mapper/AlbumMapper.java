package org.musicservice.demo.mapper;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.AlbumDto;
import org.musicservice.demo.model.music.Album;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    Album convertToAlbum(AlbumDto albumDto);
}
