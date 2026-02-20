package org.musicservice.demo.service.yandexCloud.multithreading;

import org.musicservice.demo.dto.jamendo.MusicResponse;
import org.musicservice.demo.exception.UploadObjectStorageException;
import org.musicservice.demo.jamendoIntegration.JamendoClient;
import org.musicservice.demo.service.yandexCloud.UploadDataDbService;
import org.musicservice.demo.service.yandexCloud.UploadObjectStorageService;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UploadData {

    private final JamendoClient jamendoClient;
    private final UploadDataDbService uploadDataDbService;
    private final UploadObjectStorageService uploadObjectStorageService;
    private final YandexStorageProperties yandexStorageProperties;

    @Autowired
    public UploadData(JamendoClient jamendoClient, UploadDataDbService uploadDataDbService, UploadObjectStorageService uploadObjectStorageService, YandexStorageProperties yandexStorageProperties) {
        this.jamendoClient = jamendoClient;
        this.uploadDataDbService = uploadDataDbService;
        this.uploadObjectStorageService = uploadObjectStorageService;
        this.yandexStorageProperties = yandexStorageProperties;
    }

    public void upload() {
        String tracksBucket = yandexStorageProperties.getBuckets().get("music");
        String imgBucket = yandexStorageProperties.getBuckets().get("img");
        List<MusicResponse> musicResponseList = jamendoClient.getMusicData();

        for(MusicResponse response: musicResponseList){
            try{
                String trackUrl = response.getAudiodownload();
                String mp3Key = String.format("%s/", response.getAlbum_name()) + String.format("%s.mp3", response.getName());
                response.setMp3Key(mp3Key);
                uploadObjectStorageService.uploadInObjectStorage(tracksBucket, mp3Key, trackUrl);

                String albumImgUrl = response.getAlbum_image();
                String albumImgKey = String.format("/album/%s.jpg", response.getAlbum_name());
                response.setImgKey(albumImgKey);
                uploadObjectStorageService.uploadInObjectStorage(imgBucket, albumImgKey, albumImgUrl);
            } catch (UploadObjectStorageException e){
                throw new UploadObjectStorageException("Ошибка загрузки данных в Yandex Cloud - " + e.getCause());
            }
            uploadDataDbService.insertMusicData(response);
        }
    }
}
