package org.musicservice.demo.service.yandexCloud.s3;

import org.musicservice.demo.exception.objectStorage.UploadObjectStorageException;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Primary
public class S3UploadImageService implements ObjectStorageService {

    private final YandexStorageProperties yandexStorageProperties;
    private final S3Client s3Client;

    @Autowired
    public S3UploadImageService(YandexStorageProperties yandexStorageProperties, S3Client s3Client) {
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3Client = s3Client;
    }

    @Override
    public String upload(MultipartFile file) {
        String key = "avatar/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            // Загружаем файл в бакет
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(yandexStorageProperties.getBuckets().get("img"))
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            throw new UploadObjectStorageException("Ошибка загрузки файла в s3");
        }
        return key;
    }
}
