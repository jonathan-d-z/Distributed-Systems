package com.example.testservice.client;

import com.example.testservice.dtos.OpenFoodFactsResponse;
import com.example.testservice.dtos.ProductDto;
import com.example.testservice.exceptions.OpenFoodFactsUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class OpenFoodFactsClient {

    private final RestClient restClient;

    public OpenFoodFactsClient(
            RestClient.Builder restClientBuilder,
            @Value("${openfoodfacts.user-agent:FoodNutriApp/1.0}") String userAgent) {
        this.restClient = restClientBuilder
                .baseUrl("https://world.openfoodfacts.org")
                .defaultHeader("User-Agent", userAgent)
                .build();
    }

    public Optional<ProductDto> searchByName(String name) {
        try {
            OpenFoodFactsResponse response = restClient.get()
                    .uri("/cgi/search.pl?search_terms={name}&json=1&page_size=1&fields=code,product_name,generic_name,nutriments", name)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new OpenFoodFactsUnavailableException();
                    })
                    .body(OpenFoodFactsResponse.class);

            if (response == null || response.getProducts() == null || response.getProducts().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(response.getProducts().getFirst());
        } catch (RestClientException e) {
            throw new OpenFoodFactsUnavailableException();
        }
    }
}
