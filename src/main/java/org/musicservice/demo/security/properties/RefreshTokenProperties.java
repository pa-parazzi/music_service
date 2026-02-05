package org.musicservice.demo.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "refresh-token")
@Getter
@Setter
public class RefreshTokenProperties {

    private Duration duration;
}

