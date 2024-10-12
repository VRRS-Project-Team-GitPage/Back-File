package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.RankResponse;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.RecommendationService;
import com.shinhan.VRRS.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<RankResponse> getRank(@PathVariable("vegTypeId") Integer vegTypeId) {
        return ResponseEntity.ok(recommendationService.getRank(vegTypeId));
    }

    // 카테고리 기반 추천
    @GetMapping("/category")
    public ResponseEntity<List<ProductDTO>> recommendByCategory(@RequestParam("categoryId") Integer categoryId,
                                                                @RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        List<ProductDTO> similarProducts = recommendationService.recommendByCategory(categoryId, user);
        if (similarProducts.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(similarProducts);
    }

    // 키워드 기반 추천
    @GetMapping("/keyword")
    public ResponseEntity<List<ProductDTO>> recommendByKeyword(@RequestParam("keyword") String keyword,
                                                               @RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        List<ProductDTO> recommendedProducts = recommendationService.recommendByKeyword(keyword, user);
        if (recommendedProducts.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(recommendedProducts);
    }
}