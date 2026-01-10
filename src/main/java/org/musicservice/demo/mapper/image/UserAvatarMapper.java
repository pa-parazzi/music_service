package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.entity.image.UserAvatar;

@Mapper(componentModel = "spring", uses = {UserAvatarUrlMapper.class})
public interface UserAvatarMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapUrl")
    AvatarDto convertToDto(UserAvatar userAvatar);
}
