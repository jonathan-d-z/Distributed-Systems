package com.example.testservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FoodRequestDto {
    private String code;
    private String product_name;
    private String generic_name;
    private Float quantityGrams;
}
