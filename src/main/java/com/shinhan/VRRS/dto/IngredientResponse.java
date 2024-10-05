package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IngredientResponse {
    private Integer vegTypeId;

    private String ingredients;
    private List<String> consumables;
    private List<String> nonConsumables;
    private List<String> unidentifiables;
    private List<ProductDTO> recommendations;

    public IngredientResponse(Integer vegTypeId, String ingredients, List<String> consumables, List<String> nonConsumables, List<String> unidentifiables) {
        this.vegTypeId = vegTypeId;
        this.ingredients = ingredients;
        this.consumables = consumables;
        this.nonConsumables = nonConsumables;
        this.unidentifiables = unidentifiables;
    }
}