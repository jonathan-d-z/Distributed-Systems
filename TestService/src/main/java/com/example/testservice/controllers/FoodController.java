package com.example.testservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FoodController {
    @GetMapping("/foods")
    public String getFoods(){
        return "Liste an Essen";
    }
}
