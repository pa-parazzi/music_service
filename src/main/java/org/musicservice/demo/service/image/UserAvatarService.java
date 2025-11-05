package org.musicservice.demo.service.image;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.mapper.user.AvatarMapper;
import org.musicservice.demo.model.image.UserAvatar;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.service.s3.S3UrlGenerator;
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
    private final AvatarMapper avatarMapper;
    private final S3UrlGenerator urlGenerator;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3Client s3Client;

    @Autowired
    public UserAvatarService(UserAvatarRepository userAvatarRepository, AvatarMapper avatarMapper, S3UrlGenerator urlGenerator, YandexStorageProperties yandexStorageProperties, S3Client s3Client) {
        this.userAvatarRepository = userAvatarRepository;
        this.avatarMapper = avatarMapper;
        this.urlGenerator = urlGenerator;
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
        String url = urlGenerator.generatePresignedUploadUrlImg(yandexStorageProperties.getBuckets().get("img"), key, file);
        UserAvatar userAvatar = setParams(user, key, url);
        userAvatarRepository.save(userAvatar);
    }

    @Transactional
    public void createDefaultAvatar(User user){
        String url = urlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), yandexStorageProperties.getDefaultAvatarKey());
        UserAvatar userAvatar = setParams(user, yandexStorageProperties.getDefaultAvatarKey(), url);
        userAvatarRepository.save(userAvatar);
    }

    private UserAvatar setParams(User owner, String key, String url){
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setOwner(owner);
        userAvatar.setKey(key);
        owner.setUserAvatar(userAvatar);
        AvatarDto avatarDto = avatarMapper.convertToDto(userAvatar);
        avatarDto.setUrl(url);
        avatarDto.setKey(key);
        return userAvatar;
    }

}
