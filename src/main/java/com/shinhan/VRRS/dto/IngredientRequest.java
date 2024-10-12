package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientRequest {
    private String reportNum;
    private Integer vegTypeId;
    private String ingredients;
    private boolean exists;
    private boolean fullBracket;
}