package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class RankResponse {
    private List<ProductDTO> totalRank; // 전체 인기순위
    private List<ProductDTO> vegTypeRank; // 채식유형 인기순위

    public RankResponse(List<ProductDTO> totalRank, List<ProductDTO> vegTypeRank) {
        this.totalRank = totalRank;
        this.vegTypeRank = vegTypeRank;
    }
}