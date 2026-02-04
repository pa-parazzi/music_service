package org.musicservice.demo.unit.service.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.UploadObjectStorageException;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.s3Storage.S3Storage;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAvatarServiceTest {

    @Mock
    private UserAvatarRepository userAvatarRepository;
    @Mock
    private S3Storage s3Storage;
    @Mock
    private YandexStorageProperties yandexStorageProperties;

    @InjectMocks
    private UserAvatarService userAvatarService;

    @Mock
    private MockMultipartFile mockMultipartFile;

    @Test
    void create_ShouldUploadFileInObjectStorageAndCreateUserAvatar(){
        User user = ValidUserDataFactory.user();
        when(s3Storage.upload(mockMultipartFile)).thenReturn("default_avatar");

        userAvatarService.create(mockMultipartFile, user);

        assertNotNull(user.getUserAvatar());
        verify(s3Storage).upload(mockMultipartFile);
        verify(userAvatarRepository).save(any(UserAvatar.class));
        verifyNoMoreInteractions(userAvatarRepository, s3Storage);
    }

    @Test
    void create_ShouldThrowUploadObjectStorageException(){
        User user = ValidUserDataFactory.user();

        doThrow(new UploadObjectStorageException("Ошибка загрузки файла в s3")).when(s3Storage).upload(mockMultipartFile);

        assertThrows(UploadObjectStorageException.class, ()-> userAvatarService.create(mockMultipartFile, user));

        verifyNoInteractions(userAvatarRepository);
    }

    @Test
    void createDefaultAvatar_ShouldCreateDefaultAvatar(){
        User user = ValidUserDataFactory.user();
        when(yandexStorageProperties.getDefaultAvatarKey()).thenReturn("default_avatar");

        userAvatarService.createDefaultAvatar(user);

        assertNotNull(user.getUserAvatar());
        verify(userAvatarRepository).save(any(UserAvatar.class));
    }

    @Test
    void createOrGetDefault_ShouldCreateDefaultAvatarWithoutMultipartFile(){
        User user = ValidUserDataFactory.user();
        String defaultKey = "default_avatar";
        when(yandexStorageProperties.getDefaultAvatarKey()).thenReturn(defaultKey);

        userAvatarService.createOrGetDefault(null, user);

        assertEquals(defaultKey, user.getUserAvatar().getKey());
        verify(userAvatarRepository).save(any(UserAvatar.class));
    }

    @Test
    void createOrGetDefault_ShouldCreateUserAvatarWithNewKey(){
        User user = ValidUserDataFactory.user();
        String avatarKey = "new_user_avatar_key";
        when(s3Storage.upload(mockMultipartFile)).thenReturn(avatarKey);

        userAvatarService.createOrGetDefault(mockMultipartFile, user);

        assertEquals(avatarKey, user.getUserAvatar().getKey());
        verify(s3Storage).upload(mockMultipartFile);
        verify(userAvatarRepository).save(any(UserAvatar.class));
        verifyNoMoreInteractions(s3Storage, userAvatarRepository);
    }

}
