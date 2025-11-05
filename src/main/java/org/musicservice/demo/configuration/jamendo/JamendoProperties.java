package org.musicservice.demo.configuration.jamendo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jamendo")
public class JamendoProperties {

    private String clientId;
}
