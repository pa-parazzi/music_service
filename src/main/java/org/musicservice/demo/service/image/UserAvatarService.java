package org.musicservice.demo.service.image;

import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.service.s3Storage.S3Storage;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class UserAvatarService {

    private final UserAvatarRepository userAvatarRepository;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3Storage s3Storage;

    @Autowired
    public UserAvatarService(UserAvatarRepository userAvatarRepository, YandexStorageProperties yandexStorageProperties, S3Storage s3Storage) {
        this.userAvatarRepository = userAvatarRepository;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3Storage = s3Storage;
    }

    @Transactional
    public UserAvatar create(MultipartFile file, User user){
        String key = s3Storage.upload(file);
        UserAvatar userAvatar = new UserAvatar(user, key);
        user.setUserAvatar(userAvatar);
        return userAvatarRepository.save(userAvatar);
    }

    @Transactional
    public void createDefaultAvatar(User user){
        UserAvatar userAvatar = new UserAvatar(user, yandexStorageProperties.getDefaultAvatarKey());
        user.setUserAvatar(userAvatar);
        userAvatarRepository.save(userAvatar);
    }

    @Transactional
    public void createOrGetDefault(MultipartFile file, User user){
        if(file==null){
            createDefaultAvatar(user);
            return;
        }
        create(file, user);
    }

}
