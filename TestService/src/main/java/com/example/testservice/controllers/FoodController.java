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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FoodController {
    final FoodService foodService;

    @GetMapping("/foods")
    public List<Food> getFoods(){
        return this.foodService.getAllFoods();
    }

    @PostMapping("/foods")
    public FoodResponseDto saveFood(@RequestBody FoodRequestDto foodRequestDto){
        Food food = Food.builder()
                .code(foodRequestDto.getCode())
                .product_name(foodRequestDto.getProduct_name())
                .generic_name(foodRequestDto.getGeneric_name())
                .build();
        foodService.saveFood(food);
        return FoodResponseDto.builder().code(food.getCode()).product_name(food.getProduct_name()).build();
    }

    @PostMapping("/foods/search")
    public ResponseEntity<?> searchAndSaveFood(@RequestBody SearchRequestDto searchRequest) {
        try {
            Optional<Food> foodOpt = foodService.searchAndSave(searchRequest.getName());

            if (foodOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No product found for \"" + searchRequest.getName() + "\". Try a different name."));
            }

            Food food = foodOpt.get();
            return ResponseEntity.ok(FoodResponseDto.builder()
                    .code(food.getCode())
                    .product_name(food.getProduct_name())
                    .generic_name(food.getGeneric_name())
                    .energyKcal(food.getEnergyKcal())
                    .proteins(food.getProteins())
                    .carbohydrates(food.getCarbohydrates())
                    .fat(food.getFat())
                    .sugars(food.getSugars())
                    .fiber(food.getFiber())
                    .salt(food.getSalt())
                    .build());

        } catch (OpenFoodFactsUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
