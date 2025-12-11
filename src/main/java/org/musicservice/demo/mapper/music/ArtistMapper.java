package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.response.ArtistResponse;
import org.musicservice.demo.model.music.Artist;

@Mapper(componentModel = "spring", uses = {SoundMapper.class})
public interface ArtistMapper {

    ArtistDto toDto(Artist artist);

    ArtistResponse toArtistResponse(Artist artist);
}
