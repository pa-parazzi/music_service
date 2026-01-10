package org.musicservice.demo.mapper.like;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.like.LikedAlbumResponse;
import org.musicservice.demo.entity.like.LikeAlbum;

@Mapper(componentModel = "spring")
public interface LikeAlbumMapper {

    @Mapping(target = "albumId", source = "album.id")
    LikedAlbumResponse toResponse(LikeAlbum likeAlbum);

}
