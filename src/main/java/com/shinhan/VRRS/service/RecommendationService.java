package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.RankResponse;
import com.shinhan.VRRS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ProductRepository productRepository;

    public RankResponse getRank(Integer vegTypeId) {
        Pageable pageable = PageRequest.of(0, 10);
        // 전체 채식 유형
        List<ProductDTO> totalRank = productRepository.findByTotalRank(pageable)
                .stream().map(ProductDTO::new).toList();

        // 특정 채식 유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i = 1; i<= vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        List<ProductDTO> vegTypeRank =  productRepository.findByVegTypeRank(vegTypeIds, pageable)
                .stream().map(ProductDTO::new).toList();

        return new RankResponse(totalRank, vegTypeRank);
    }
}
