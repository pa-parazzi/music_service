package org.musicservice.demo.service.uploadData;

import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.integration.jamendo.JamendoClient;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.musicservice.demo.service.music.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicImportService {

    private final GenreService genreService;
    private final MusicCatalogService musicCatalogService;
    private final JamendoClient jamendoClient;
    private final TrackMetadataWriter trackMetadataWriter;
    private final S3KeyGenerator s3KeyGenerator;

    @Autowired
    public MusicImportService(GenreService genreService, MusicCatalogService musicCatalogService,
                              JamendoClient jamendoClient, TrackMetadataWriter trackMetadataWriter, S3KeyGenerator s3KeyGenerator) {
        this.genreService = genreService;
        this.musicCatalogService = musicCatalogService;
        this.jamendoClient = jamendoClient;
        this.trackMetadataWriter = trackMetadataWriter;
        this.s3KeyGenerator = s3KeyGenerator;
    }

    public void importProcess(String genreName) {
        Genre genre = genreService.findGenreByName(genreName);
        List<MusicResponse> responseList = filterMusicResponse(jamendoClient.tracksPack(genreName));
        for (MusicResponse response : responseList) {
            MusicResponse responseWithKeys = response.withKeys
                    (s3KeyGenerator.generateUploadMp3Key(), s3KeyGenerator.generateUploadAlbumImageKey());
            if(musicCatalogService.saveMusicData(responseWithKeys, genre)){
                TrackMetadata metadata = buildTrackMetadata(responseWithKeys);
                trackMetadataWriter.write(metadata);
            }
        }
    }

    private TrackMetadata buildTrackMetadata(MusicResponse response){
        return new TrackMetadata(response.audiodownload(), response.album_image(),
                response.mp3Key(), response.albumImgKey());
    }

    private List<MusicResponse> filterMusicResponse(List<MusicResponse> responseList){
        return responseList.stream()
                .filter(response -> response.audiodownload_allowed() == true)
                .filter(response -> response.duration() > 0)
                .toList();
    }

}
