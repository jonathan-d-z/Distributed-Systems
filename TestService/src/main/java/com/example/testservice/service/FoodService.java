package com.example.testservice.service;

import com.example.testservice.entities.Food;
import com.example.testservice.repositories.FoodRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    final FoodRepository foodRepository;

    public List<Food> getAllFoods(){
        return foodRepository.findAll();
    }

    public void saveFood(Food food){
        foodRepository.save(food);
    }
}
