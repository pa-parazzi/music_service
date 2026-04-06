package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundPageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoundService {

    private final SoundRepository soundRepository;
    private final SoundLikeRepository soundLikeRepository;
    private final SoundMapper soundMapper;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public SoundService(SoundRepository soundRepository, SoundLikeRepository soundLikeRepository, SoundMapper soundMapper, ArtistRepository artistRepository, AlbumRepository albumRepository) {
        this.soundRepository = soundRepository;
        this.soundLikeRepository = soundLikeRepository;
        this.soundMapper = soundMapper;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
    }

    public PageResponse<SoundResponse> getSoundsByArtistIdPaged(Long artistId, int page, int size){
        if(!artistRepository.existsById(artistId)) throw new MusicNotFoundException("Исполнитель не найден");
        Page<Sound> soundPage = soundRepository.findByArtistId
                (artistId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<SoundResponse> response = soundPage.getContent().stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(response, soundPage.hasNext());
    }

    public PageResponse<SoundResponse> getSoundsByAlbumIdPaged(Long albumId, int page, int size){
        if(!albumRepository.existsById(albumId)) throw new MusicNotFoundException("Альбом не найден");
        Page<Sound> soundPage = soundRepository.findByAlbumId
                (albumId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<SoundResponse> response = soundPage.getContent().stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(response, soundPage.hasNext());
    }

    public PageResponse<SoundResponse> getTrackCollectionByUserId(Long userId, int page, int size){
        Page<SoundLike> soundLikePage = soundLikeRepository
                .findByUserIdOrderByCreatedAtDescIdDesc(userId, PageRequest.of(page, size));
        List<SoundLike> soundLikes = soundLikePage.getContent();
        if(soundLikes.isEmpty()) throw new NoSuchMusicResultException("У вас нет понравившихся песен");
        List<SoundResponse> response = soundLikes.stream().map(SoundLike::getSound).map(soundMapper::toResponse).toList();
        return new PageResponse<>(response, soundLikePage.hasNext());
    }

    public SoundPageResponse getSoundPageResponseById(Long id) {
        Sound sound = soundRepository.findByIdForSoundPage(id).orElseThrow(()-> new MusicNotFoundException("Песня не найдена"));
        return soundMapper.toPageResponse(sound);
    }

    public PageResponse<SoundResponse> getSoundsByGenreIdPaged(Long genreId, int page, int size){
        Page<Sound> soundPage = soundRepository.findByGenreId
                (genreId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<SoundResponse> soundResponseList = soundPage.getContent().stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(soundResponseList, soundPage.hasNext());
    }
}
