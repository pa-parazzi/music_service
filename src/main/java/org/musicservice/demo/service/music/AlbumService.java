package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.album.AlbumsResponse;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
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
    private final GenreService genreService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper, GenreService genreService) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.genreService = genreService;
    }

    public AlbumsResponse getAlbumCollectionByUserLikes(LikedContentIds contentIds){
        if(contentIds == null || contentIds.ids().isEmpty()) throw new NoSuchMusicResultException("У вас нет понравившихся альбомов");
        List<Long> orderIds = contentIds.ids(); // порядок элементов сохранен
        List<AlbumResponse> unorderResponse = albumRepository.findAllByIdForCollectionPage(orderIds).stream().map(albumMapper::toAlbumResponse).toList(); // порядок элементов не сохранился
        Map<Long, AlbumResponse> mapById = unorderResponse.stream().collect(Collectors.toMap(AlbumResponse::getAlbumId, Function.identity()));
        List<AlbumResponse> orderedResponse = orderIds.stream().map(mapById::get).toList();
        return new AlbumsResponse(orderedResponse);
    }

    public AlbumsResponse getAllAlbumsByMainResponse(){
        List<AlbumResponse> albumResponseList = albumRepository.findAllForMainPage().stream().map(albumMapper::toAlbumResponse).toList();
        return new AlbumsResponse(albumResponseList);
    }

    public AlbumResponse findByIdWithArtistAndImage(Long id){
        return albumRepository.findByIdWithArtistAndImage(id).map(albumMapper::toAlbumResponse).orElseThrow(()->new MusicNotFoundException("Album with id: " + id + " not found"));
    }

    public AlbumsResponse findAllAlbumsByGenreId(Long genreId){
        genreService.checkExistById(genreId);
        List<AlbumResponse> albumResponseList = albumRepository.findAllByGenreId(genreId).stream().map(albumMapper::toAlbumResponse).toList();
        return new AlbumsResponse(albumResponseList);
    }
}
