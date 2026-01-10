package org.musicservice.demo.security.refreshToken;

import org.musicservice.demo.Authority.Authority;

import java.time.Instant;

public interface RefreshTokenProjection {

    Long getId();
    Instant getExpiryDate();
    Long getUserId();
    Authority getRole();
}
