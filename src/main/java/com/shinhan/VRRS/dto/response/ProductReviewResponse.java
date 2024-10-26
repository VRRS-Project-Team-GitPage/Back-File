package com.shinhan.VRRS.dto.response;

import com.shinhan.VRRS.dto.ReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ProductReviewResponse {
    private ReviewDTO review; // 사용자 리뷰
    private List<ReviewDTO> reviews; // 전체 리뷰
}
