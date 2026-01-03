package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.like.LikedAlbumResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.album.CollectionAlbumsResponse;
import org.musicservice.demo.dto.music.album.MainAlbumResponse;
import org.musicservice.demo.exception.music.AlbumNotFoundException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Long> ids = responses.stream().map(LikedAlbumResponse::getAlbumId).toList();
        List<AlbumResponse> albumResponses =  albumRepository.findAllByIdWithArtistAndImage(ids).stream().map(albumMapper::toAlbumResponse).toList();
        CollectionAlbumsResponse collectionAlbums = new CollectionAlbumsResponse();
        collectionAlbums.setAlbums(albumResponses);
        return collectionAlbums;
    }

    public MainAlbumResponse getAllAlbumsByMainResponse(){
        List<Album> albums = albumRepository.findAllForMainPage();
        List<AlbumResponse> albumResponseList = albums.stream().map(albumMapper::toAlbumResponse).toList();
        MainAlbumResponse mainAlbumResponse =  new MainAlbumResponse();
        mainAlbumResponse.setAlbums(albumResponseList);
        return mainAlbumResponse;
    }

    public Album searchById(Long albumId){
        return albumRepository.searchById(albumId).orElseThrow(()->new AlbumNotFoundException("Альбом не существует"));
    }

    public AlbumResponse getAlbumById(Long id){
        Album album = albumRepository.findByIdWithArtistAndImage(id).orElseThrow(()->new AlbumNotFoundException("Альбом не существует"));
        return albumMapper.toAlbumResponse(album);
    }
}
