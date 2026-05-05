package com.example.testservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FoodResponseDto {
    private String code;
    private String product_name;
    private String generic_name;
    private Float energyKcal;
    private Float proteins;
    private Float carbohydrates;
    private Float fat;
    private Float sugars;
    private Float fiber;
    private Float salt;
}
