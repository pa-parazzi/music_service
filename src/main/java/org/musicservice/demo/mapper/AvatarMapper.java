package org.musicservice.demo.mapper;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.model.image.Avatar;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    Avatar convertToObj(AvatarDto avatarDto);

    AvatarDto convertToDto(Avatar avatar);
}
