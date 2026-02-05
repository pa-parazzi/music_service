package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.image.AlbumImageResponse;
import org.musicservice.demo.entity.image.AlbumImage;

@Mapper(componentModel = "spring", uses = {ImageUrlMapper.class})
public interface AlbumImageMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapUrl")
    AlbumImageResponse convertToDto(AlbumImage albumImage);
}
