package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.image.SoundImageDto;
import org.musicservice.demo.model.image.SoundImage;

@Mapper(componentModel = "spring")
public interface SoundImageMapper {

    SoundImageDto convertToDto(SoundImage soundImage);
}
