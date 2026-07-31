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

    public Optional<Food> searchAndSave(String name, Float quantityGrams) {
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
        float grams = quantityGrams != null && quantityGrams > 0 ? quantityGrams : 100f;

        Food food = Food.builder()
                .code(product.getCode())
                .product_name(product.getProductName())
                .generic_name(product.getGenericName())
                .quantityGrams(grams)
                .energyKcal(scale(n != null ? n.getEnergyKcal() : null, grams))
                .proteins(scale(n != null ? n.getProteins() : null, grams))
                .carbohydrates(scale(n != null ? n.getCarbohydrates() : null, grams))
                .fat(scale(n != null ? n.getFat() : null, grams))
                .sugars(scale(n != null ? n.getSugars() : null, grams))
                .fiber(scale(n != null ? n.getFiber() : null, grams))
                .salt(scale(n != null ? n.getSalt() : null, grams))
                .build();

        foodRepository.save(food);
        return Optional.of(food);
    }

    private Float scale(Float valuePer100g, float grams) {
        return valuePer100g != null ? valuePer100g * grams / 100f : null;
    }
}
