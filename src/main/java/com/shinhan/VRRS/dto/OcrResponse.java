package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OcrResponse {
    private String productName;
    private String ingredients;
    private boolean isFullBracket;

    public OcrResponse(String productName, List<String> ingredients, boolean isFullBracket) {
        this.productName = productName;
        this.ingredients = String.join(", ", ingredients);
        this.isFullBracket = isFullBracket;
    }
}
