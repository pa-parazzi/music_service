package org.musicservice.demo.mapper;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.model.music.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    Artist convertToArtist(ArtistDto artistDto);
}
