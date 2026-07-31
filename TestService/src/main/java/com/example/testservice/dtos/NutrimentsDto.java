package com.example.testservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NutrimentsDto {

    @JsonProperty("energy-kcal_100g")
    private Float energyKcal;

    @JsonProperty("proteins_100g")
    private Float proteins;

    @JsonProperty("carbohydrates_100g")
    private Float carbohydrates;

    @JsonProperty("fat_100g")
    private Float fat;

    @JsonProperty("sugars_100g")
    private Float sugars;

    @JsonProperty("fiber_100g")
    private Float fiber;

    @JsonProperty("salt_100g")
    private Float salt;
}
