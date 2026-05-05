package com.example.testservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {

    private String code;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("generic_name")
    private String genericName;

    private NutrimentsDto nutriments;
}
