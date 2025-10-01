package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.AlbumDto;
import org.musicservice.demo.dto.music.MusicDto;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.mapper.AlbumMapper;
import org.musicservice.demo.mapper.ArtistMapper;
import org.musicservice.demo.mapper.SoundMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class MusicService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SoundRepository soundRepository;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final SoundMapper soundMapper;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3TrackUrlGenerator s3TrackUrlGenerator;

    @Autowired
    public MusicService(ArtistRepository artistRepository, AlbumRepository albumRepository, SoundRepository soundRepository, ArtistMapper artistMapper, AlbumMapper albumMapper, SoundMapper soundMapper, YandexStorageProperties yandexStorageProperties, S3TrackUrlGenerator s3TrackUrlGenerator) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
        this.artistMapper = artistMapper;
        this.albumMapper = albumMapper;
        this.soundMapper = soundMapper;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3TrackUrlGenerator = s3TrackUrlGenerator;
    }

    public List<SoundDto> soundList(){
        return soundRepository.findAll().stream().map(sound -> {
            String url = s3TrackUrlGenerator.generatePresignedUrl(yandexStorageProperties.getBuckets().get("music"), sound.getKey());
            SoundDto soundDto = soundMapper.convertToDto(sound);
            soundDto.setUrl(url);
            return soundDto;
        }).toList();
    }

    public SoundDto getSound(Long id){
        Sound sound = soundRepository.findById(id).orElseThrow(()->new RuntimeException("Песня не найдена"));
        String url = s3TrackUrlGenerator.generatePresignedUrl(yandexStorageProperties.getBuckets().get("music"), sound.getKey());
        SoundDto soundDto = soundMapper.convertToDto(sound);
        soundDto.setUrl(url);
        return soundDto;
    }

    @Transactional
    public void create(MusicDto musicDto){
        Artist artist = artistMapper.convertToArtist(musicDto.getArtist());
        artistRepository.save(artist);

        List<AlbumDto> albums = musicDto.getAlbums();
        for(AlbumDto album: albums){
            Album newAlbum = albumMapper.convertToAlbum(album);
            newAlbum.setArtist(artist);
            albumRepository.save(newAlbum);

            List<SoundDto> soundList = musicDto.getSoundList();
            for(SoundDto sound: soundList){
                Sound newSound = soundMapper.convertToSound(sound);
                newSound.setAlbum(newAlbum);
                newSound.setArtist(artist);
                soundRepository.save(newSound);
            }
        }

    }

}
