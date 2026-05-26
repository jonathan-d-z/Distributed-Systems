package com.example.testservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProfileAuthClient {

    private final RestClient restClient;

    public ProfileAuthClient(
            RestClient.Builder restClientBuilder,
            @Value("${profile.service.url}") String profileServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(profileServiceUrl)
                .build();
    }

    public boolean isAuthorized(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }

        try {
            return restClient.get()
                    .uri("/profiles/validate")
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (RestClientException e) {
            return false;
        }
    }
}
