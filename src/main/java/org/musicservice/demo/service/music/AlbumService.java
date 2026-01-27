package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.like.LikedAlbumResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.album.CollectionAlbumsResponse;
import org.musicservice.demo.dto.music.album.MainAlbumResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.exception.ApiNotFoundException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    public CollectionAlbumsResponse getAlbumCollectionByUserLikes(List<LikedAlbumResponse> responses){
        List<Long> orderIds = responses.stream().map(LikedAlbumResponse::getAlbumId).toList(); // порядок элементов сохранен
        List<AlbumResponse> unorderResponse =  albumRepository.findAllByIdForCollectionPage(orderIds).stream().map(albumMapper::toAlbumResponse).toList(); // порядок элементов не сохранился
        Map<Long, AlbumResponse> mapById = unorderResponse.stream().collect(Collectors.toMap(AlbumResponse::getAlbumId, Function.identity()));
        List<AlbumResponse> response = orderIds.stream().map(mapById::get).toList();
        return new CollectionAlbumsResponse(response);
    }

    public MainAlbumResponse getAllAlbumsByMainResponse(){
        List<AlbumResponse> albumResponseList = albumRepository.findAllForMainPage().stream().map(albumMapper::toAlbumResponse).toList();
        return new MainAlbumResponse(albumResponseList);
    }

    public Album searchById(Long albumId){
        return albumRepository.searchById(albumId).orElseThrow(()->new ApiNotFoundException("Album with id: " + albumId + " not found"));
    }

    public AlbumResponse getAlbumById(Long id){
        return albumRepository.findByIdWithArtistAndImage(id).map(albumMapper::toAlbumResponse).orElseThrow(()->new ApiNotFoundException("Album with id: " + id + " not found"));
    }
}
