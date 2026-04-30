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


}
