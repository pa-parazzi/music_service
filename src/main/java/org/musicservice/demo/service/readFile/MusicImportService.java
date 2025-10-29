package org.musicservice.demo.service.readFile;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.UploadMusicResponse;
import org.musicservice.demo.jamendoIntegration.JamendoClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicImportService {

    private final JamendoClient jamendoClient;
    private final YandexUploadMusic yandexUploadMusic;
    private final YandexStorageProperties yandexStorageProperties;

    public MusicImportService(JamendoClient jamendoClient, YandexUploadMusic yandexUploadMusic, YandexStorageProperties yandexStorageProperties) {
        this.jamendoClient = jamendoClient;
        this.yandexUploadMusic = yandexUploadMusic;
        this.yandexStorageProperties = yandexStorageProperties;
    }

    public void importMusic() throws Exception {
        List<UploadMusicResponse> uploadMusicResponses = jamendoClient.getMusicFormatJson();
        try {
            for(UploadMusicResponse response: uploadMusicResponses){

                String mp3Key = String.format("%s/", response.getAlbum_name()) + response.getName() + ".mp3";
                yandexUploadMusic.uploadMusicForBucket(yandexStorageProperties.getBuckets().get("music"), mp3Key, response.getAudio());

                String imgKey = "albums/" + response.getAlbum_name() + ".jpg";
                yandexUploadMusic.uploadMusicForBucket(yandexStorageProperties.getBuckets().get("img"), imgKey, response.getAlbum_image());

            }
        } catch (Exception e){
            throw new Exception("Ошибка загрузки данных в yandexcloud: " + e.getMessage());
        }
    }
}
