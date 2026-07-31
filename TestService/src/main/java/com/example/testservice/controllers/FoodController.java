package com.example.testservice.controllers;

import com.example.testservice.dtos.FoodRequestDto;
import com.example.testservice.dtos.FoodResponseDto;
import com.example.testservice.dtos.SearchRequestDto;
import com.example.testservice.entities.Food;
import com.example.testservice.exceptions.OpenFoodFactsUnavailableException;
import com.example.testservice.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FoodController {
    final FoodService foodService;

    @GetMapping("/foods")
    public ResponseEntity<?> getFoods(){
        return ResponseEntity.ok(this.foodService.getAllFoods());
    }

    @PostMapping("/foods")
    public ResponseEntity<?> saveFood(
            @RequestBody FoodRequestDto foodRequestDto){
        Food food = Food.builder()
                .code(foodRequestDto.getCode())
                .product_name(foodRequestDto.getProduct_name())
                .generic_name(foodRequestDto.getGeneric_name())
                .quantityGrams(foodRequestDto.getQuantityGrams())
                .build();
        foodService.saveFood(food);
        return ResponseEntity.ok(toDto(food));
    }

    @PostMapping("/foods/search")
    public ResponseEntity<?> searchAndSaveFood(
            @RequestBody SearchRequestDto searchRequest) {
        try {
            Optional<Food> foodOpt = foodService.searchAndSave(searchRequest.getName(), searchRequest.getQuantityGrams());

            if (foodOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No product found for \"" + searchRequest.getName() + "\". Try a different name."));
            }

            return ResponseEntity.ok(toDto(foodOpt.get()));

        } catch (OpenFoodFactsUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private FoodResponseDto toDto(Food food) {
        return FoodResponseDto.builder()
                .id(food.getId())
                .code(food.getCode())
                .product_name(food.getProduct_name())
                .generic_name(food.getGeneric_name())
                .quantityGrams(food.getQuantityGrams())
                .energyKcal(food.getEnergyKcal())
                .proteins(food.getProteins())
                .carbohydrates(food.getCarbohydrates())
                .fat(food.getFat())
                .sugars(food.getSugars())
                .fiber(food.getFiber())
                .salt(food.getSalt())
                .build();
    }
}
