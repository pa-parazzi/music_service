package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundPageProjection;
import org.musicservice.demo.dto.music.sound.SoundPageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.TracksResponse;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
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
public class SoundService {

    private final SoundRepository soundRepository;
    private final SoundMapper soundMapper;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public SoundService(SoundRepository soundRepository, SoundMapper soundMapper, ArtistRepository artistRepository, AlbumRepository albumRepository) {
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
    }

    public TracksResponse getSoundListByArtistId(Long artistId){
        if(!artistRepository.existsById(artistId)) throw new MusicNotFoundException("Исполнитель не найден");
        List<SoundResponse> soundResponseList = soundRepository.findAllByArtistId(artistId).stream().map(soundMapper::toResponse).toList();
        return new TracksResponse(soundResponseList);
    }

    public TracksResponse getSoundListByAlbumId(Long albumId){
        if(!albumRepository.existsById(albumId)) throw new MusicNotFoundException("Альбом не найден");
        List<SoundResponse> soundResponseList =  soundRepository.findAllByAlbumId(albumId).stream().map(soundMapper::toResponse).toList();
        return new TracksResponse(soundResponseList);
    }

    public TracksResponse getTrackCollectionByUserLikes(LikedContentIds contentIds){
        if(contentIds == null || contentIds.ids().isEmpty()) throw new NoSuchMusicResultException("У вас нет понравившихся песен");
        List<Long> soundOrderIds = contentIds.ids();
        Long[] orderIds = soundOrderIds.toArray(Long[]::new);
        List<SoundResponse> response = soundRepository.findAllByIdForCollectionPage(orderIds).stream().map(soundMapper::toResponse).toList();
        return new TracksResponse(response);
    }

    public SoundPageResponse viewById(Long id) {
        SoundPageProjection soundPageProjection = soundRepository.findByIdForSoundPage(id).orElseThrow(()-> new MusicNotFoundException("Песня не найдена"));
        return soundMapper.toPageResponse(soundPageProjection);
    }

    public PageResponse<SoundResponse> findTracksByGenreIdPaged(Long genreId, int page, int size){
        Page<Sound> soundPage = soundRepository.findAllByGenreId
                (genreId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<SoundResponse> soundResponseList = soundPage.getContent().stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(soundResponseList, soundPage.hasNext());
    }
}
