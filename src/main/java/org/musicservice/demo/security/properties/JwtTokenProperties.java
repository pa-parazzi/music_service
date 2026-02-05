package org.musicservice.demo.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtTokenProperties {

    private String jwtSecretKey;
    private String issuer;
    private long leewaySeconds;
    private Duration accessTokenDuration;
}
