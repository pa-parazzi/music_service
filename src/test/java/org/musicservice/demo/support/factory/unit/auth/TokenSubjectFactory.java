package org.musicservice.demo.support.factory.unit.auth;

import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.security.dto.TokenSubject;

import java.util.List;

public class TokenSubjectFactory {

    public static TokenSubject tokenSubject(){
        return new TokenSubject(1L, List.of(Authority.USER.getAuthority()));
    }
}
