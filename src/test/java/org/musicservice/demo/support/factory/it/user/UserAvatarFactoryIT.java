package org.musicservice.demo.support.factory.it.user;

import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;

import java.util.UUID;

public class UserAvatarFactoryIT {

    public static UserAvatar userAvatar(User user){
        return new UserAvatar(user, UUID.randomUUID() + ".jpg");
    }
}
