package org.musicservice.demo.jamendoIntegration;

import org.musicservice.demo.dto.jamendo.JamendoResponse;
import org.musicservice.demo.dto.jamendo.UploadMusicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class JamendoClient {

    private final WebClient webClient;
    private final JamendoProperties jamendoProperties;
    private final HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(30));

    @Autowired
    public JamendoClient(WebClient.Builder builder, JamendoProperties jamendoProperties) {
        this.webClient = builder
                .baseUrl("https://api.jamendo.com/v3.0")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
        this.jamendoProperties = jamendoProperties;
    }

    public List<UploadMusicResponse> getMusicData(){
        return Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/tracks/")
                                .queryParam("client_id", jamendoProperties.getClientId())
                                .queryParam("format", "json")
                                .queryParam("limit", 10)
                                .queryParam("imagesize", 600)
                                .build())
                        .retrieve()
                        .bodyToMono(JamendoResponse.class)
                        .block())
                .getResults();
    }
}
