package org.musicservice.demo.service.user;

import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.service.yandexCloud.s3.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserAvatarService {

    private final UserAvatarRepository userAvatarRepository;
    private final ObjectStorageService objectStorageService;

    @Autowired
    public UserAvatarService(UserAvatarRepository userAvatarRepository, ObjectStorageService objectStorageService) {
        this.userAvatarRepository = userAvatarRepository;
        this.objectStorageService = objectStorageService;
    }

    @Transactional
    public UserAvatar create(MultipartFile file, User user){
        String key = objectStorageService.upload(file);
        return userAvatarRepository.save(new UserAvatar(user, key));
    }

}
