package org.musicservice.demo.configuration.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth-token")
public class AuthenticationTokenProperties {

    private Duration refreshTokenDuration;
    private Duration accessTokenDuration;
}
