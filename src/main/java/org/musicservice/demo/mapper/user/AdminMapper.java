package org.musicservice.demo.mapper.user;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.admin.AdminDto;
import org.musicservice.demo.entity.user.User;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    User convertToUser(AdminDto adminDto);

    AdminDto convertToAdmin(User user);
}
