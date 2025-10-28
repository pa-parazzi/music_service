package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.*;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.mapper.music.ArtistMapper;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.image.AlbumImageService;
import org.musicservice.demo.service.s3.S3ImgUrlGenerator;
import org.musicservice.demo.service.image.SoundImageService;
import org.musicservice.demo.service.readFile.MusicReaderManager;
import org.musicservice.demo.service.s3.S3TrackUrlGenerator;
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
    private final AlbumImageService albumImageService;
    private final SoundImageService soundImageService;
    private final AlbumService albumService;
    private final SoundService soundService;

    @Autowired
    public MusicService(ArtistRepository artistRepository, AlbumRepository albumRepository, SoundRepository soundRepository, AlbumImageService albumImageService, SoundImageService soundImageService, AlbumService albumService, SoundService soundService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
        this.albumImageService = albumImageService;
        this.soundImageService = soundImageService;
        this.albumService = albumService;
        this.soundService = soundService;
    }

    public MainResponse viewMusic(){
        MainResponse mainResponse = new MainResponse();
        List<SoundDto> soundDtoList = soundService.soundList();
        List<AlbumResponse> albumResponse = albumService.getAlbums(soundDtoList);
        mainResponse.setAlbums(albumResponse);
        return mainResponse;
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
            //artistRepository.save(artist);

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
            albumImageService.create(file, album);
            soundImageService.create(file, sound);
        }
    }

}
