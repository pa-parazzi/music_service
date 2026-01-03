package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.model.image.UserAvatar;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    UserAvatar convertToObj(AvatarDto avatarDto);

    AvatarDto convertToDto(UserAvatar userAvatar);
}
