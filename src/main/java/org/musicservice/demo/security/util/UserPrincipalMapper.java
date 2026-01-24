package org.musicservice.demo.security.util;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class UserPrincipalMapper {

    public static UserPrincipal from(User user){
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isAccountNonLocked(),
                user.isEnabled(),
                List.of(new SimpleGrantedAuthority(user.getRole().getAuthority())));
    }
}
