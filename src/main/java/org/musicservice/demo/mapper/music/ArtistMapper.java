package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.mainResponse.ArtistResponse;
import org.musicservice.demo.model.music.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    ArtistDto toDto(Artist artist);

    ArtistResponse toArtistResponse(Artist artist);
}
