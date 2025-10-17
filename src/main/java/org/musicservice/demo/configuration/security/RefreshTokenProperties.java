package org.musicservice.demo.configuration.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "refresh-token")
public class RefreshTokenProperties {

    private Duration duration;
}
