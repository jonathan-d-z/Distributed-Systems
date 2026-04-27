package com.example.testservice.entities;

import jakarta.persistence.Entity;
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
    private Long id;
    //General Info
    private String code;
    private String product_name;
    private String generic_name;
    private float quantity;
    private String brands,brands_tag;
    private String categories,categories_tag;
    private String countries,countries_tag;
    private String languages,languages_tag;
    private String url;
    //Nutriments
    //private Nutriments nutriments;
    //...https://de.openfoodfacts.org/produkt/4008400401621/nutella


}
