package com.shinhan.VRRS.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IngredientRequest {
    private String reportNum;
    @NotNull
    @Min(1) @Min(6)
    private Integer vegTypeId;
    @NotNull
    private String ingredients;
    @NotNull
    private boolean exists;
    @NotNull
    private boolean fullBracket;
}