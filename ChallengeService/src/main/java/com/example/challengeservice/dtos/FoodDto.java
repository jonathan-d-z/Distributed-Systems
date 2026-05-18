package com.example.challengeservice.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodDto {
    private String code;
    private String product_name;
    private Float energyKcal;
    private Float proteins;
    private Float carbohydrates;
    private Float fat;
    private Float sugars;
    private Float fiber;
    private Float salt;
}
