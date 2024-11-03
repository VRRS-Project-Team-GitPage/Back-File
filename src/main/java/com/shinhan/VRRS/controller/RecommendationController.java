package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.response.RankResponse;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.RecommendationService;
import com.shinhan.VRRS.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserService userService;

    // 인기순위 제공
    @GetMapping("/rank/{vegTypeId}")
    public ResponseEntity<RankResponse> getRank(@PathVariable("vegTypeId") @Min(1) @Max(6) Integer vegTypeId) {
        return ResponseEntity.ok(recommendationService.getRank(vegTypeId));
    }

    // 카테고리 기반 추천
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ProductDTO> recommendByCategory(@RequestHeader("Authorization") String jwt,
                                                                @PathVariable("categoryId")
                                                                @Min(1) @Max(4) Integer categoryId) {
        User user = userService.getUserFromJwt(jwt);
        ProductDTO similarProducts = recommendationService.recommendByCategory(categoryId, user);
        if (similarProducts == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        return ResponseEntity.ok(similarProducts);
    }

    // 키워드 기반 추천
    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<ProductDTO> recommendByKeyword(@RequestHeader("Authorization") String jwt,
                                                         @PathVariable("keyword") String keyword) {
        keyword = keyword.trim();
        if (keyword.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        User user = userService.getUserFromJwt(jwt);
        ProductDTO recommendedProducts = recommendationService.recommendByKeyword(keyword, user);
        if (recommendedProducts == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        return ResponseEntity.ok(recommendedProducts);
    }
}