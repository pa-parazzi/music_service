package org.musicservice.demo.service.yandex;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.jamendo.UploadMusicResponse;
import org.musicservice.demo.jamendoIntegration.JamendoClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicImportInYandexCloud {

    private final JamendoClient jamendoClient;
    private final YandexUploadMusic yandexUploadMusic;
    private final YandexStorageProperties yandexStorageProperties;
    private final UploadMusic uploadMusic;

    public MusicImportInYandexCloud(JamendoClient jamendoClient, YandexUploadMusic yandexUploadMusic, YandexStorageProperties yandexStorageProperties, UploadMusic uploadMusic) {
        this.jamendoClient = jamendoClient;
        this.yandexUploadMusic = yandexUploadMusic;
        this.yandexStorageProperties = yandexStorageProperties;
        this.uploadMusic = uploadMusic;
    }

    public void importMusic() throws Exception {
        List<UploadMusicResponse> uploadMusicResponses = jamendoClient.getMusicFormatJson();
        try {
            for(UploadMusicResponse response: uploadMusicResponses){

                String mp3Key = String.format("%s/", response.getAlbum_name()) + response.getName() + ".mp3";
                yandexUploadMusic.uploadMusicForBucket(yandexStorageProperties.getBuckets().get("music"), mp3Key, response.getAudiodownload());
                response.setMp3Key(mp3Key);

                String imgKey = "albums/" + response.getAlbum_name() + ".jpg";
                response.setImgKey(imgKey);
                yandexUploadMusic.uploadMusicForBucket(yandexStorageProperties.getBuckets().get("img"), imgKey, response.getAlbum_image());

                uploadMusic.insertMusicData(response);
            }
        } catch (Exception e){
            throw new Exception("Ошибка загрузки данных в yandexcloud: " + e.getMessage());
        }
    }
}
