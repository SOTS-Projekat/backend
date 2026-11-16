package com.sots.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    // Defaults to the local Flask app; in Docker it is overridden with KST_URL=http://kst:5000
    @Value("${kst.url:http://127.0.0.1:5000}")
    private String flaskEndpointUrl;

    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .baseUrl(flaskEndpointUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
