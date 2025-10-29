package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.*;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
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
    private final AlbumService albumService;
    private final SoundService soundService;

    @Autowired
    public MusicService(ArtistRepository artistRepository, AlbumRepository albumRepository, SoundRepository soundRepository, AlbumService albumService, SoundService soundService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
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

//    public Artist getArtist(UploadMusicResponse musicDto){
//        return artistRepository.findByName(musicDto.getArtist())
//                .orElseGet(()-> {
//                    Artist newArtist = new Artist();
//                    newArtist.setName(musicDto.getArtist());
//                    return newArtist;
//                });
//    }
//
//    public Album getAlbum(UploadMusicResponse musicDto){
//        return albumRepository.findByTitle(musicDto.getAlbum())
//                .orElseGet(() -> {
//                    Album newAlbum = new Album();
//                    newAlbum.setTitle(musicDto.getAlbum());
//                    return newAlbum;
//                });
//    }
//
//    public Sound getSound(UploadMusicResponse musicDto){
//        return soundRepository.findByTitle(musicDto.getTitle())
//                .orElseGet(()-> {
//                    Sound newSound = new Sound();
//                    newSound.setTitle(musicDto.getTitle());
//                    newSound.setDuration(musicDto.getDuration());
//                    newSound.setKey(musicDto.getS3_key());
//                    return newSound;
//                });
//    }
//
//    @Transactional
//    public void importFile(MultipartFile file) throws IOException {
//        List<UploadMusicResponse> musicDtoList = MusicReaderManager.readToParse(file);
//        for(UploadMusicResponse musicDto: musicDtoList){
//            Artist artist = getArtist(musicDto);
//
//            Album album = getAlbum(musicDto);
//            album.setArtist(artist);
//
//            Sound sound = getSound(musicDto);
//            sound.setArtist(artist);
//            sound.setAlbum(album);
//
//            if(!artist.getAlbums().contains(album)){
//                artist.getAlbums().add(album);
//            }
//
//            if(!album.getSoundList().contains(sound)){
//                album.getSoundList().add(sound);
//            }
//
//            if(!artist.getSoundList().contains(sound)){
//                artist.getSoundList().add(sound);
//            }
//
//            artistRepository.save(artist);
//        }
//    }

}
