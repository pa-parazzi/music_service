package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.model.music.Sound;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SoundMapper {

    List<SoundDto> toDtoList(List<Sound> soundList);

    SoundDto toDto(Sound sound);
}
