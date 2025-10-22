package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.AlbumDto;
import org.musicservice.demo.dto.music.MusicDto;
import org.musicservice.demo.dto.music.MusicInsertDto;
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
import org.musicservice.demo.service.readFile.MusicReaderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


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

    public Artist getArtist(MusicInsertDto musicDto){
        return artistRepository.findByName(musicDto.getArtist())
                .orElseGet(()-> {
                    Artist newArtist = new Artist();
                    newArtist.setName(musicDto.getArtist());
                    return newArtist;
                });
    }

    public Album getAlbum(MusicInsertDto musicDto){
        return albumRepository.findByTitle(musicDto.getAlbum())
                .orElseGet(() -> {
                    Album newAlbum = new Album();
                    newAlbum.setTitle(musicDto.getAlbum());
                    return newAlbum;
                });
    }

    public Sound getSound(MusicInsertDto musicDto){
        return soundRepository.findByTitle(musicDto.getTitle())
                .orElseGet(()-> {
                    Sound newSound = new Sound();
                    newSound.setTitle(musicDto.getTitle());
                    newSound.setDuration(musicDto.getDuration());
                    newSound.setKey(musicDto.getS3_key());
                    return newSound;
                });
    }

    @Transactional
    public void importFile(MultipartFile file) throws IOException {
        List<MusicInsertDto> musicDtoList = MusicReaderManager.readToInsert(file);
        for(MusicInsertDto musicDto: musicDtoList){
            Artist artist = getArtist(musicDto);
            artistRepository.save(artist);

            Album album = getAlbum(musicDto);
            album.setArtist(artist);

            Sound sound = getSound(musicDto);
            sound.setArtist(artist);
            sound.setAlbum(album);

            if(!artist.getAlbums().contains(album)){
                artist.getAlbums().add(album);
            }

            if(!album.getSoundList().contains(sound)){
                album.getSoundList().add(sound);
            }

            if(!artist.getSoundList().contains(sound)){
                artist.getSoundList().add(sound);
            }

            artistRepository.save(artist);
        }
    }

}
