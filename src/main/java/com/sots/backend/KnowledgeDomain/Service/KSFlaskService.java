package com.sots.backend.KnowledgeDomain.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KSFlaskService {
    private final WebClient webClient;

    public Mono<int[][]> getIITAImplications(int[][] matrix) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return webClient
                .post()
                .uri("/iita")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(matrix))
                .retrieve()
                .bodyToMono(int[][].class);
    }

    public Mono<String> getIitaRich(Map<String, Object> payload) {  //  Metoda koja salje vise stvari u IITa i vraca imena cvorova, domen, ne samo uredjene parove (ovo nam je bitno zbog generisanja ontologije).
        return webClient
                .post()
                .uri("/iita/rich")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(String.class);
    }
}
