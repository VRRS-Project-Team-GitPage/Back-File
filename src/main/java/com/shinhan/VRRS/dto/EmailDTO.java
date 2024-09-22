package com.shinhan.VRRS.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailDTO {
    private String to; // 수신자
    private String subject; // 제목
    private String message; // 내용
}