package org.musicservice.demo.service.image;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.mapper.user.AvatarMapper;
import org.musicservice.demo.model.image.Avatar;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.image.AvatarRepository;
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
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final AvatarMapper avatarMapper;
    private final S3ImgUrlGenerator urlGenerator;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3Client s3Client;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, AvatarMapper avatarMapper, S3ImgUrlGenerator urlGenerator, YandexStorageProperties yandexStorageProperties, S3Client s3Client) {
        this.avatarRepository = avatarRepository;
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
            throw new RuntimeException("Ошибка загрузки файла в S3", e);
        }
        String url = urlGenerator.generatePresignedUploadUrlImg(yandexStorageProperties.getBuckets().get("img"), key, file);
        Avatar avatar = setParams(user, key, url);
        avatarRepository.save(avatar);
    }

    @Transactional
    public void createDefaultAvatar(User user){
        String url = urlGenerator.generatePresignedUrl(yandexStorageProperties.getBuckets().get("img"), yandexStorageProperties.getDefaultAvatarKey());
        Avatar avatar = setParams(user, yandexStorageProperties.getDefaultAvatarKey(), url);
        avatarRepository.save(avatar);
    }

    private Avatar setParams(User owner, String key, String url){
        Avatar avatar = new Avatar();
        avatar.setOwner(owner);
        avatar.setKey(key);
        owner.setAvatar(avatar);
        AvatarDto avatarDto = avatarMapper.convertToDto(avatar);
        avatarDto.setUrl(url);
        avatarDto.setKey(key);
        return avatar;
    }

}
