package com.example.testservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
