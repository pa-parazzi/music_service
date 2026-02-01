package org.musicservice.demo.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.login")
@Getter
@Setter
public class LoginSecurityProperties {

    private int maxFailedAttempts;
    private int lockDurationMinutes;
}
