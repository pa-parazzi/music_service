package org.musicservice.demo.service.readFile;

import lombok.Setter;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Service
public class YandexUploadMusic {

    private final S3Client s3Client;
    @Autowired
    public YandexUploadMusic(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadMusicForBucket(String bucket, String key, String fileUrl) {
        try(InputStream inputStream = new URL(fileUrl).openStream()){
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    RequestBody.fromInputStream(inputStream, inputStream.available()));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки треков в бакет : " + e.getMessage());
        }
    }


}
