package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.service.likes.AlbumLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumLikeService albumLikeService;
    private final AlbumMapper albumMapper;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumLikeService albumLikeService, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumLikeService = albumLikeService;
        this.albumMapper = albumMapper;
    }

    public PageResponse<AlbumResponse> getAlbumCollectionByUserId(Long userId, int page, int size){
        Page<AlbumLike> pageResponse = albumLikeService.findAlbumLikesByUserId(userId, PageRequest.of(page, size));
        List<AlbumResponse> albumListResponse = pageResponse.getContent()
                .stream().map(AlbumLike::getAlbum).map(albumMapper::toAlbumResponse).toList();
        return new PageResponse<>(albumListResponse, pageResponse.hasNext());
    }

    public AlbumResponse findByIdWithArtistAndImage(Long id){
        return albumRepository.findByIdWithArtistAndImage(id).map(albumMapper::toAlbumResponse)
                .orElseThrow(()->new MusicNotFoundException("Album with id: " + id + " not found"));
    }

    public PageResponse<AlbumResponse> findAlbumsByGenreIdPaged(Long genreId, int page, int size){
        Page<Album> pageResponse = albumRepository.findByGenreId
                (genreId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<Album> albums = pageResponse.getContent();
        if(albums.isEmpty()) throw new NoSuchMusicException("Альбомов по такому жанру не найдено");
        List<AlbumResponse> albumResponseList = albums.stream().map(albumMapper::toAlbumResponse).toList();
        return new PageResponse<>(albumResponseList, pageResponse.hasNext());
    }
}
