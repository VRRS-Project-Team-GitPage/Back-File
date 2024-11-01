package com.shinhan.VRRS.dto.ect;

import com.shinhan.VRRS.dto.ReviewDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PreviewReview {
    private Integer reviewIndex;
    private List<ReviewDTO> reviews;
}
