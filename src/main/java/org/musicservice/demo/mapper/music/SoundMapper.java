package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.sound.SoundDto;
import org.musicservice.demo.model.music.Sound;

import java.util.List;

@Mapper(componentModel = "spring", uses = SoundTrackUrlMapper.class)
public interface SoundMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapUrl")
    List<SoundDto> toDtoList(List<Sound> soundList);

    @Mapping(target = "url", source = "key", qualifiedByName = "mapUrl")
    SoundDto toDto(Sound sound);
}
