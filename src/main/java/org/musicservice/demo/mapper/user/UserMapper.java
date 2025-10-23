package org.musicservice.demo.mapper.user;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.user.UserDtoForLogin;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User convertFromUserDtoForRegistrationToUser(UserDtoForRegistration userDtoForRegistration);

    UserDtoForView getUserDtoForView(User user);

    User convertFromUserForLogin(UserDtoForLogin userDtoForLogin);

}
