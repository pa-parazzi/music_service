package org.musicservice.demo.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "verification-token")
@Getter
@Setter
public class VerificationTokenProperties {

    private Duration expirationHours;
    private String activationUrl;
}
