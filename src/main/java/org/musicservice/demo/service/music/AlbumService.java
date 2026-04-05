package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumLikeRepository albumLikeRepository;
    private final AlbumMapper albumMapper;
    private final GenreService genreService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumLikeRepository albumLikeRepository, AlbumMapper albumMapper, GenreService genreService) {
        this.albumRepository = albumRepository;
        this.albumLikeRepository = albumLikeRepository;
        this.albumMapper = albumMapper;
        this.genreService = genreService;
    }

    public PageResponse<AlbumResponse> getAlbumCollectionByUserId(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<AlbumLike> albumLikePage = albumLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
        if(albumLikePage.isEmpty()) throw new NoSuchMusicResultException("У вас нет понравившихся альбомов");
        List<AlbumResponse> albumListResponse = albumLikePage.stream().map(AlbumLike::getAlbum).map(albumMapper::toAlbumResponse).toList();
        return new PageResponse<>(albumListResponse, albumLikePage.hasNext());
    }

    public AlbumResponse findByIdWithArtistAndImage(Long id){
        return albumRepository.findByIdWithArtistAndImage(id).map(albumMapper::toAlbumResponse).orElseThrow(()->new MusicNotFoundException("Album with id: " + id + " not found"));
    }

    public PageResponse<AlbumResponse> findAlbumsByGenreIdPaged(Long genreId, int page, int size){
        genreService.checkExistById(genreId);
        Page<Album> pageResponse = albumRepository.findAllByGenreId(genreId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<AlbumResponse> albumResponseList = pageResponse.getContent().stream().map(albumMapper::toAlbumResponse).toList();
        return new PageResponse<>(albumResponseList, pageResponse.hasNext());
    }
}
