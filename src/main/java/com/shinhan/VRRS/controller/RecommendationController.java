package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.RankResponse;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.RecommendationService;
import com.shinhan.VRRS.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserService userService;

    // 인기순위
    @GetMapping("/rank/{vegTypeId}")
    public ResponseEntity<RankResponse> getRank(@PathVariable("vegTypeId") @Min(1) @Max(6) Integer vegTypeId) {
        return ResponseEntity.ok(recommendationService.getRank(vegTypeId));
    }

    // 카테고리 기반 추천
    @GetMapping("/category")
    public ResponseEntity<List<ProductDTO>> recommendByCategory(@RequestHeader("Authorization") String jwt,
                                                                @RequestParam("categoryId")
                                                                @Min(1) @Max(4) Integer categoryId) {
        User user = userService.getUserFromJwt(jwt);
        List<ProductDTO> similarProducts = recommendationService.recommendByCategory(categoryId, user);
        if (similarProducts.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(similarProducts);
    }

    // 키워드 기반 추천
    @GetMapping("/keyword")
    public ResponseEntity<List<ProductDTO>> recommendByKeyword(@RequestHeader("Authorization") String jwt,
                                                               @RequestParam("keyword") String keyword) {
        User user = userService.getUserFromJwt(jwt);
        List<ProductDTO> recommendedProducts = recommendationService.recommendByKeyword(keyword, user);
        if (recommendedProducts.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(recommendedProducts);
    }
}