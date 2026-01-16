package org.musicservice.demo.security.refreshToken;

import java.time.Instant;

public interface RefreshTokenProjection {
    Long getId();
    Instant getExpiryDate();
    Long getUserId();
}
