package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Sound;

@Mapper(componentModel = "spring", uses = SoundTrackUrlMapper.class)
public interface SoundMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapUrl")
    SoundResponse toResponse(Sound sound);
}
