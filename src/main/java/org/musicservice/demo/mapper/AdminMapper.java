package org.musicservice.demo.mapper;

import org.mapstruct.Mapper;
import org.musicservice.demo.dto.admin.AdminDto;
import org.musicservice.demo.model.user.User;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    User convertToUser(AdminDto adminDto);

    AdminDto convertToAdmin(User user);
}
