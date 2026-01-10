package org.musicservice.demo.security.util;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.dto.TokenSubject;

import java.util.List;

public final class TokenSubjectMapper {

    public static TokenSubject from(User user){
        return new TokenSubject(user.getId(), List.of(user.getRole().getAuthority()));
    }
}
