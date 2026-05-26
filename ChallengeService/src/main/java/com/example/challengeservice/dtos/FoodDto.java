package com.example.challengeservice.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodDto {
    private Long id;
    private String code;
    private String product_name;
    private String generic_name;
    private Float quantityGrams;
    private Float energyKcal;
    private Float proteins;
    private Float carbohydrates;
    private Float fat;
    private Float sugars;
    private Float fiber;
    private Float salt;
}
