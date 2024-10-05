package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientRequest {
    private String productName;
    private Integer vegTypeId;
    private String ingredients;
    private boolean isFullBracket;
}