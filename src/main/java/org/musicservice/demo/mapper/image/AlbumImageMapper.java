package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.entity.image.AlbumImage;

@Mapper(componentModel = "spring", uses = {AlbumImageUrlMapper.class})
public interface AlbumImageMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapUrl")
    AlbumImageDto convertToDto(AlbumImage albumImage);

}
