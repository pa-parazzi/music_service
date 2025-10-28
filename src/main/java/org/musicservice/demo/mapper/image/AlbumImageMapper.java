package org.musicservice.demo.mapper.image;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.model.image.AlbumImage;

@Mapper(componentModel = "spring")
public interface AlbumImageMapper {

    AlbumImageDto convertToDto(AlbumImage albumImage);

    AlbumImage convertToObj (AlbumImageDto albumImageDto);
}
