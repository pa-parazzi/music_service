package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.*;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional(readOnly = true)
public class MusicService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SoundRepository soundRepository;
    private final AlbumService albumService;
    private final AlbumImageRepository albumImageRepository;
    private final SoundService soundService;

    @Autowired
    public MusicService(ArtistRepository artistRepository, AlbumRepository albumRepository, SoundRepository soundRepository, AlbumService albumService, AlbumImageRepository albumImageRepository, SoundService soundService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
        this.albumService = albumService;
        this.albumImageRepository = albumImageRepository;
        this.soundService = soundService;
    }

    public MainResponse viewMusic(){
        MainResponse mainResponse = new MainResponse();
        List<SoundDto> soundDtoList = soundService.soundList();
        List<AlbumResponse> albumResponse = albumService.getAlbums(soundDtoList);
        mainResponse.setAlbums(albumResponse);
        return mainResponse;
    }

    public Artist getArtist(UploadMusicResponse response){
        return artistRepository.findByName(response.getArtist_name()).orElseGet(()-> {
            Artist artist = new Artist();
            artist.setName(response.getArtist_name());
            return artist;
        });
    }

    public Album getAlbum(UploadMusicResponse response){
        return albumRepository.findByTitle(response.getAlbum_name())
                .orElseGet(() -> {
                    Album newAlbum = new Album();
                    newAlbum.setTitle(response.getAlbum_name());
                    return newAlbum;
                });
    }

    public AlbumImage getAlbumImage(UploadMusicResponse response){
        return albumImageRepository.findByS3Key(response.getImgKey())
                .orElseGet(()-> {
                    AlbumImage albumImage = new AlbumImage();
                    albumImage.setS3Key(response.getImgKey());
                    return albumImage;
                });
    }

    public Sound getSound(UploadMusicResponse response){
        return soundRepository.findByTitle(response.getName())
                .orElseGet(()-> {
                    Sound newSound = new Sound();
                    newSound.setTitle(response.getName());
                    newSound.setDuration(response.getDuration());
                    newSound.setKey(response.getMp3Key());
                    return newSound;
                });
    }

    @Transactional
    public void insertMusicData(UploadMusicResponse response) {
        Artist artist = getArtist(response);

        Album album = getAlbum(response);
        album.setArtist(artist);

        AlbumImage albumImage = getAlbumImage(response);
        albumImage.setAlbum(album);
        album.setImage(albumImage);

        Sound sound = getSound(response);
        sound.setArtist(artist);
        sound.setAlbum(album);

        if(!artist.getAlbums().contains(album)){
            artist.getAlbums().add(album);
        }

        if(!artist.getSoundList().contains(sound)){
            artist.getSoundList().add(sound);
        }

        if(!album.getSoundList().contains(sound)){
            album.getSoundList().add(sound);
        }

        if(!albumImage.getAlbum().equals(album)){
            albumImage.setAlbum(album);
        }

        artistRepository.save(artist);
        albumRepository.save(album);
    }

}
