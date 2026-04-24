package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundPageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.SoundsResponse;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.likes.SoundLikeService;
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
    private final SoundLikeService soundLikeService;
    private final SoundMapper soundMapper;

    @Autowired
    public SoundService(SoundRepository soundRepository, SoundLikeService soundLikeService, SoundMapper soundMapper) {
        this.soundRepository = soundRepository;
        this.soundLikeService = soundLikeService;
        this.soundMapper = soundMapper;
    }

    public SoundsResponse getSoundsByAlbumId(Long albumId){
        List<SoundResponse> sounds = soundRepository.findAllByAlbumId(albumId)
                .stream().map(soundMapper::toResponse).toList();
        if(sounds.isEmpty()) throw new MusicNotFoundException("Треки не найдены");
        return new SoundsResponse(sounds);
    }

    private PageResponse<SoundResponse> toPageResponse(Page<Sound> soundsPage){
        List<Sound> sounds = soundsPage.getContent();
        if(sounds.isEmpty()) throw new MusicNotFoundException("Треки не найдены");
        List<SoundResponse> response = sounds.stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(response, soundsPage.hasNext());
    }

    public PageResponse<SoundResponse> getSoundsByArtistIdPaged(Long artistId, int page, int size){
        Page<Sound> soundsPage = soundRepository.findByArtistId
                (artistId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        return toPageResponse(soundsPage);
    }

    public PageResponse<SoundResponse> getSoundsByGenreIdPaged(Long genreId, int page, int size){
        Page<Sound> soundsPage = soundRepository.findByGenreId
                (genreId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        return toPageResponse(soundsPage);
    }

    public PageResponse<SoundResponse> getTrackCollectionByUserId(Long userId, int page, int size){
        Page<SoundLike> pageResponse = soundLikeService.findSoundLikesByUserid(userId, PageRequest.of(page, size));
        List<SoundResponse> response = pageResponse.getContent()
                .stream().map(SoundLike::getSound).map(soundMapper::toResponse).toList();
        return new PageResponse<>(response, pageResponse.hasNext());
    }

    public SoundPageResponse getSoundPageResponseById(Long id) {
        Sound sound = soundRepository.findByIdForSoundPage(id)
                .orElseThrow(()-> new MusicNotFoundException("Песня не найдена"));
        return soundMapper.toPageResponse(sound);
    }
}
