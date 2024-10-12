package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OcrResponse {
    private String reportNum;
    private String ingredients;
    private boolean exists;
    private boolean fullBracket;

    public OcrResponse(String reportNum, List<String> ingredients, boolean fullBracket) {
        this.reportNum = reportNum;
        this.ingredients = ingredients.toString();
        this.exists = false;
        this.fullBracket = fullBracket;
    }
}
