package org.musicservice.demo.integration.jamendo;

import org.musicservice.demo.integration.jamendo.response.JamendoResponse;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
public class JamendoClient {

    private final WebClient webClient;
    private final JamendoProperties jamendoProperties;

    @Autowired
    public JamendoClient(WebClient.Builder builder, JamendoProperties jamendoProperties) {
        this.webClient = builder.baseUrl("https://api.jamendo.com/v3.0").build();
        this.jamendoProperties = jamendoProperties;
    }

    public List<MusicResponse> tracksPack(String genreName){
        return Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/tracks/")
                                .queryParam("client_id", jamendoProperties.getClientId())
                                .queryParam("format", "json")
                                .queryParam("limit", 50)
                                .queryParam("imagesize", 600)
                                .queryParam("tags", genreName)
                                .build())
                        .retrieve()
                        .bodyToMono(JamendoResponse.class)
                        .block())
                .results();
    }
}
