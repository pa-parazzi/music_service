package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.entity.image.AlbumImage;

@Mapper(componentModel = "spring", uses = {ImageUrlMapper.class})
public interface AlbumImageMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapImgUrl")
    ImageResponse convertToDto(AlbumImage albumImage);
}
