package com.example.challengeservice.client;

import com.example.challengeservice.dtos.FoodDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class FoodServiceClient {

    private final RestClient restClient;

    public FoodServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${food.service.url}") String foodServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(foodServiceUrl)
                .build();
    }

    public List<FoodDto> getAllFoods(String authorizationHeader) {
        return restClient.get()
                .uri("/foods")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(new ParameterizedTypeReference<List<FoodDto>>() {});
    }
}
