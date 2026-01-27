package org.musicservice.demo.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.mapper.image.UserAvatarMapper;
import org.musicservice.demo.entity.user.User;

@Mapper(componentModel = "spring", uses = {UserAvatarMapper.class})
public interface UserMapper {

    @Mapping(target = "avatar", source = "userAvatar")
    UserMainResponse toMainResponse(User user);

}
