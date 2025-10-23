package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.model.music.Sound;

@Mapper(componentModel = "spring")
public interface SoundMapper {

    Sound convertToSound(SoundDto soundDto);

    SoundDto convertToDto(Sound sound);
}
