package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.entity.image.UserAvatar;

@Mapper(componentModel = "spring", uses = {ImageUrlMapper.class})
public interface ImageMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapImgUrl")
    ImageResponse toImageResponse(UserAvatar userAvatar);
}
