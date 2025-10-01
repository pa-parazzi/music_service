package org.musicservice.demo.Authority;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
