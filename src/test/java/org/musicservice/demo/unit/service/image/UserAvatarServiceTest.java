package org.musicservice.demo.unit.service.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.UploadObjectStorageException;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.service.yandexCloud.s3.ObjectStorageService;
import org.musicservice.demo.support.factory.unit.user.UserDataFactory;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAvatarServiceTest {

    @Mock
    private UserAvatarRepository userAvatarRepository;
    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private YandexStorageProperties yandexStorageProperties;

    @InjectMocks
    private UserAvatarService userAvatarService;

    @Mock
    private MockMultipartFile mockMultipartFile;

    @Test
    void create_ShouldUploadFileInObjectStorageAndCreateUserAvatar(){
        User user = UserDataFactory.user();
        String avatarKey = "default_avatar";
        when(objectStorageService.upload(mockMultipartFile)).thenReturn(avatarKey);

        userAvatarService.create(mockMultipartFile, user);

        verify(objectStorageService).upload(mockMultipartFile);

        ArgumentCaptor<UserAvatar> avatarCaptor = ArgumentCaptor.forClass(UserAvatar.class);
        verify(userAvatarRepository).save(avatarCaptor.capture());
        UserAvatar userAvatar = avatarCaptor.getValue();

        assertEquals(user, userAvatar.getOwner());
        assertEquals(avatarKey, userAvatar.getKey());
    }

    @Test
    void create_ShouldThrowUploadObjectStorageException(){
        User user = UserDataFactory.user();

        doThrow(new UploadObjectStorageException("Ошибка загрузки файла в s3")).when(objectStorageService).upload(mockMultipartFile);

        assertThrows(UploadObjectStorageException.class, ()-> userAvatarService.create(mockMultipartFile, user));

        verifyNoInteractions(userAvatarRepository);
    }

    @Test
    void createDefaultAvatar_ShouldCreateDefaultAvatar(){
        User user = UserDataFactory.user();
        String avatarKey = "default_avatar";
        when(yandexStorageProperties.getDefaultAvatarKey()).thenReturn(avatarKey);

        userAvatarService.createDefaultAvatar(user);

        ArgumentCaptor<UserAvatar> avatarCaptor = ArgumentCaptor.forClass(UserAvatar.class);
        verify(userAvatarRepository).save(avatarCaptor.capture());
        UserAvatar userAvatar = avatarCaptor.getValue();

        assertEquals(user, userAvatar.getOwner());
        assertEquals(avatarKey, userAvatar.getKey());
    }

    @Test
    void createOrGetDefault_ShouldCreateDefaultAvatar_WhenMultipartFileIsNull(){
        User user = UserDataFactory.user();
        String defaultKey = "default_avatar";
        when(yandexStorageProperties.getDefaultAvatarKey()).thenReturn(defaultKey);

        userAvatarService.createOrGetDefault(null, user);

        ArgumentCaptor<UserAvatar> avatarCapture = ArgumentCaptor.forClass(UserAvatar.class);
        verify(userAvatarRepository).save(avatarCapture.capture());
        UserAvatar userAvatar = avatarCapture.getValue();
        assertEquals(user, userAvatar.getOwner());
        assertEquals(defaultKey, userAvatar.getKey());

        verifyNoInteractions(objectStorageService);
    }

    @Test
    void createOrGetDefault_ShouldCreateUserAvatar_WhenMultipartFileIsPresent(){
        User user = UserDataFactory.user();
        String avatarKey = "new_user_avatar_key";
        when(objectStorageService.upload(mockMultipartFile)).thenReturn(avatarKey);

        userAvatarService.createOrGetDefault(mockMultipartFile, user);

        verify(objectStorageService).upload(mockMultipartFile);

        ArgumentCaptor<UserAvatar> avatarCapture = ArgumentCaptor.forClass(UserAvatar.class);
        verify(userAvatarRepository).save(avatarCapture.capture());
        UserAvatar userAvatar = avatarCapture.getValue();
        assertEquals(user, userAvatar.getOwner());
        assertEquals(avatarKey, userAvatar.getKey());
    }

}
