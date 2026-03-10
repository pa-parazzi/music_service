package org.musicservice.demo.storage.s3;

import org.musicservice.demo.exception.objectStorage.UploadObjectStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service(value = "uploadImage")
public class S3UploadImageService implements ObjectStorageService {

    private final YandexStorageProperties yandexStorageProperties;
    private final S3Client s3Client;

    @Autowired
    public S3UploadImageService(YandexStorageProperties yandexStorageProperties, S3Client s3Client) {
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3Client = s3Client;
    }

    @Override
    public void upload(String s3Key, String bucket, UploadS3Object uploadS3Object) {
        if(s3Key.equals(yandexStorageProperties.getDefaultAvatarKey())) return;
        try (InputStream inputStream = uploadS3Object.inputStream()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3Key)
                            .contentType(uploadS3Object.contentType())
                            .build(),
                    RequestBody.fromInputStream(inputStream, uploadS3Object.contentSize()));
        } catch (IOException e) {
            throw new UploadObjectStorageException("Ошибка загрузки изображения");
        }
    }
}
