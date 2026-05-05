package com.example.testservice.client;

import com.example.testservice.dtos.OpenFoodFactsResponse;
import com.example.testservice.dtos.ProductDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class OpenFoodFactsClient {

    private final RestClient restClient;

    public OpenFoodFactsClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://world.openfoodfacts.org")
                .defaultHeader("User-Agent", "FoodNutriApp/1.0 (lorenz.brach@gmail.com)")
                .build();
    }

    public Optional<ProductDto> searchByName(String name) {
        OpenFoodFactsResponse response = restClient.get()
                .uri("/cgi/search.pl?search_terms={name}&json=1&page_size=1&fields=code,product_name,generic_name,nutriments", name)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    // swallow 4xx/5xx from OpenFoodFacts (e.g. 503 rate limit) — treat as no result
                })
                .body(OpenFoodFactsResponse.class);

        if (response == null || response.getProducts() == null || response.getProducts().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.getProducts().getFirst());
    }
}
