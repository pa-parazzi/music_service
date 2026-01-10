package org.musicservice.demo.service.image;

import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserAvatarService {

    private final UserAvatarRepository userAvatarRepository;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3Client s3Client;

    @Autowired
    public UserAvatarService(UserAvatarRepository userAvatarRepository, YandexStorageProperties yandexStorageProperties, S3Client s3Client) {
        this.userAvatarRepository = userAvatarRepository;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3Client = s3Client;
    }

    @Transactional
    public void create(MultipartFile file, User user){
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
            throw new RuntimeException("Ошибка загрузки аватара в S3", e);
        }
        UserAvatar userAvatar = new UserAvatar(user, key);
        user.setUserAvatar(userAvatar);
        userAvatarRepository.save(userAvatar);
    }

    @Transactional
    public void createDefaultAvatar(User user){
        UserAvatar userAvatar = new UserAvatar(user, yandexStorageProperties.getDefaultAvatarKey());
        user.setUserAvatar(userAvatar);
        userAvatarRepository.save(userAvatar);
    }

    @Transactional
    public void createOrGet(MultipartFile file, User user){
        if(file==null){
            createDefaultAvatar(user);
            return;
        }
        create(file, user);
    }

}
