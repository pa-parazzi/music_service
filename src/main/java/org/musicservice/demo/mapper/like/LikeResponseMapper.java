package org.musicservice.demo.mapper.like;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.model.music.Like;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LikeResponseTargetDataMapper.class})
public interface LikeResponseMapper {

    @Mapping(target = "targetType", source = "target", qualifiedByName = "mapTargetType")
    @Mapping(target = "targetId", source = "target", qualifiedByName = "mapTargetId")
    LikeResponse toResponse(Like like);
}
