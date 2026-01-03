package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.model.music.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    ArtistResponse toResponse(Artist artist);

}
