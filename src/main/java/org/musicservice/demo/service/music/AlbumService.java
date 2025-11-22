package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.exception.music.AlbumNotFoundException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.service.image.AlbumImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumImageService albumImageService;
    private final AlbumMapper albumMapper;
    private final SoundService soundService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumImageService albumImageService, AlbumMapper albumMapper, SoundService soundService) {
        this.albumRepository = albumRepository;
        this.albumImageService = albumImageService;
        this.albumMapper = albumMapper;
        this.soundService = soundService;
    }

    public List<AlbumResponse> getAllAlbumResponse(){
        List<Album> albums = albumRepository.findAll();
        return albums.stream().map(album -> {
            Long albumId = album.getId();
            AlbumImageDto albumImageDto = albumImageService.getAlbumImageByAlbumId(albumId);
            List<SoundDto> soundDtoList = soundService.getSoundListByAlbumId(albumId);
            AlbumResponse albumResponse = albumMapper.toAlbumResponse(album);
            albumResponse.setAlbumId(albumId);
            albumResponse.setAlbumImage(albumImageDto);
            albumResponse.setSoundList(soundDtoList);
            return albumResponse;
        }).toList();
    }

    private Album searchById(Long albumId){
        return albumRepository.searchById(albumId).orElseThrow(()->new AlbumNotFoundException("Альбом не существует"));
    }

    public AlbumResponse getAlbumById(Long albumId){
        Album album = searchById(albumId);
        AlbumResponse albumResponse = albumMapper.toAlbumResponse(album);
        AlbumImageDto albumImageDto = albumImageService.getAlbumImageByAlbumId(albumId);
        List<SoundDto> soundDtoList = soundService.getSoundListByAlbumId(albumId);
        albumResponse.setAlbumId(albumId);
        albumResponse.setAlbumImage(albumImageDto);
        albumResponse.setSoundList(soundDtoList);
        return albumResponse;
    }
}
