package com.example.testservice.service;

import com.example.testservice.client.OpenFoodFactsClient;
import com.example.testservice.dtos.NutrimentsDto;
import com.example.testservice.dtos.ProductDto;
import com.example.testservice.entities.Food;
import com.example.testservice.repositories.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodService {

    final FoodRepository foodRepository;
    final OpenFoodFactsClient openFoodFactsClient;

    public List<Food> getAllFoods(){
        return foodRepository.findAll();
    }

    public void saveFood(Food food){
        foodRepository.save(food);
    }

    public Optional<Food> searchAndSave(String name) {
        Optional<ProductDto> productOpt = openFoodFactsClient.searchByName(name);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductDto product = productOpt.get();

        // skip products without a barcode (can't use as primary key)
        if (product.getCode() == null || product.getCode().isBlank()) {
            return Optional.empty();
        }

        NutrimentsDto n = product.getNutriments();

        Food food = Food.builder()
                .code(product.getCode())
                .product_name(product.getProductName())
                .generic_name(product.getGenericName())
                .energyKcal(n != null ? n.getEnergyKcal() : null)
                .proteins(n != null ? n.getProteins() : null)
                .carbohydrates(n != null ? n.getCarbohydrates() : null)
                .fat(n != null ? n.getFat() : null)
                .sugars(n != null ? n.getSugars() : null)
                .fiber(n != null ? n.getFiber() : null)
                .salt(n != null ? n.getSalt() : null)
                .build();

        foodRepository.save(food);
        return Optional.of(food);
    }
}
