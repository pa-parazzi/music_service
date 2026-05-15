package org.musicservice.demo.integration.jamendo;

import org.musicservice.demo.integration.jamendo.response.JamendoResponse;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class JamendoClient {

    private final RestClient restClient;
    private final JamendoProperties jamendoProperties;

    @Autowired
    public JamendoClient(RestClient.Builder builder, JamendoProperties jamendoProperties) {
        this.restClient = builder.baseUrl("https://api.jamendo.com/v3.0").build();
        this.jamendoProperties = jamendoProperties;
    }

    public List<MusicResponse> tracksPack(String genreName){
        JamendoResponse response = restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/tracks/")
                                .queryParam("client_id", jamendoProperties.getClientId())
                                .queryParam("format", "json")
                                .queryParam("limit", 50)
                                .queryParam("imagesize", 600)
                                .queryParam("tags", genreName)
                                .build())
                        .retrieve()
                        .body(JamendoResponse.class);
        return Objects.requireNonNull(response).results();
    }
}
