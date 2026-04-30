package com.example.testservice.controllers;

import com.example.testservice.dtos.FoodRequestDto;
import com.example.testservice.dtos.FoodResponseDto;
import com.example.testservice.entities.Food;
import com.example.testservice.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        Food food = Food.builder().code(foodRequestDto.getCode()).product_name(foodRequestDto.getProduct_name()).generic_name(foodRequestDto.getGeneric_name()).quantity(foodRequestDto.getQuantity()).build();
        foodService.saveFood(food);
        return FoodResponseDto.builder().code(food.getCode()).product_name(food.getProduct_name()).build();
    }
}
