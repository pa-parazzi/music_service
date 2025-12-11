package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.model.music.Like;

@Mapper(componentModel = "spring", uses = {LikeResponseObjMapper.class})
public interface LikeResponseMapper {

    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUserId")
    @Mapping(target = "targetType", source = "target", qualifiedByName = "mapTargetType")
    @Mapping(target = "targetId", source = "target", qualifiedByName = "mapTargetId")
    LikeResponse toResponse(Like like);
}
