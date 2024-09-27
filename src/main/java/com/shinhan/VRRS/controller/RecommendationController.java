package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.RankResponse;
import com.shinhan.VRRS.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{vegTypeId}")
    public ResponseEntity<RankResponse> getRank(@PathVariable("vegTypeId") Integer vegTypeId) {
        return ResponseEntity.ok(recommendationService.getRank(vegTypeId));
    }
}
