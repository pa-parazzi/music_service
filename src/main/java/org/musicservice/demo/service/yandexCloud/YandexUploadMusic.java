package org.musicservice.demo.service.yandexCloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class YandexUploadMusic {

    private final S3Client s3Client;

    @Autowired
    public YandexUploadMusic(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadMusicForBucket(String bucket, String key, String fileUrl) {
        HttpURLConnection connection = null;
        try{
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true); // разрешаем редиректы
            connection.connect();

            int responseCode = connection.getResponseCode();
            if(responseCode!= HttpURLConnection.HTTP_OK){
                throw new RuntimeException("Ошибка чтения url : " + fileUrl + " - " + responseCode);
            }

        try(InputStream inputStream = connection.getInputStream()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("audio/mpeg") // для mp3
                            .build(),
                    RequestBody.fromInputStream(inputStream, connection.getContentLengthLong()));
        }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки треков в бакет : " + e.getMessage());
        } finally {
            if(connection!=null){
                connection.disconnect();
            }
        }
    }


}
