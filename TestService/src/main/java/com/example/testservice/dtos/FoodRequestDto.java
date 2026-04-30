package com.example.testservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FoodRequestDto {
    private String code;
    private String product_name;
    private String generic_name;
    private float quantity;


}
