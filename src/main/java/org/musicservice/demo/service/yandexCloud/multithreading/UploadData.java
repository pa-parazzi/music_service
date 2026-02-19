package org.musicservice.demo.service.yandexCloud.multithreading;

import org.musicservice.demo.dto.jamendo.UploadMusicResponse;
import org.musicservice.demo.jamendoIntegration.JamendoClient;
import org.musicservice.demo.service.yandexCloud.UploadDataDbService;
import org.musicservice.demo.service.yandexCloud.UploadObjectStorageService;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Service
public class UploadData {

    private final JamendoClient jamendoClient;
    private final UploadDataDbService uploadDataDbService;
    private final UploadObjectStorageService uploadObjectStorageService;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3Client s3Client;

    @Autowired
    public UploadData(JamendoClient jamendoClient, UploadDataDbService uploadDataDbService, UploadObjectStorageService uploadObjectStorageService, YandexStorageProperties yandexStorageProperties, S3Client s3Client) {
        this.jamendoClient = jamendoClient;
        this.uploadDataDbService = uploadDataDbService;
        this.uploadObjectStorageService = uploadObjectStorageService;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3Client = s3Client;
    }

    public void upload() throws IOException {
        List<UploadMusicResponse> musicResponseList = jamendoClient.getMusicData();
        InputStream inputStream = null;
        HttpsURLConnection connection = null;

        for(UploadMusicResponse response: musicResponseList){
            String mp3Key = String.format("%s/", response.getAlbum_name()) + response.getName() + ".mp3";
            try{
                URI uri = new URI(response.getAudiodownload());
                URL url = uri.toURL();
                connection = (HttpsURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(20000);
                connection.connect();

                int responseStatus = connection.getResponseCode();
                if(responseStatus==HttpsURLConnection.HTTP_OK){
                    inputStream = connection.getInputStream();
                    s3Client.putObject(PutObjectRequest.builder()
                            .contentType("audio/mpeg")
                            .key(mp3Key)
                            .bucket(yandexStorageProperties.getBuckets().get("music"))
                            .build(), RequestBody.fromInputStream(inputStream, connection.getContentLengthLong()));
                }
            } catch (RuntimeException | URISyntaxException |MalformedURLException e){
                throw new RuntimeException(e);
            } finally {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }
            }
        }
    }

//    public void upload(){
//        List<UploadMusicResponse> musicResponseList = jamendoClient.getMusicData();
//        musicResponseList.forEach(response -> executorService.submit(()-> process(response)));
//    }

    @Transactional
    protected void process(UploadMusicResponse response){
        String mp3Key = String.format("%s/", response.getAlbum_name()) + response.getName() + ".mp3";
        response.setMp3Key(mp3Key);
        uploadObjectStorageService.upload(yandexStorageProperties.getBuckets().get("music"), mp3Key, response.getAudiodownload());

        String imgKey = "albums/" + response.getAlbum_name() + ".jpg";
        response.setImgKey(imgKey);
        uploadObjectStorageService.upload(yandexStorageProperties.getBuckets().get("img"), imgKey, response.getAlbum_image());

        uploadDataDbService.insertMusicData(response);
    }
}
