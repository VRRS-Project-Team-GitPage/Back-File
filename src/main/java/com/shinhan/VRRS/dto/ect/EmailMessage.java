package com.shinhan.VRRS.dto.ect;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class EmailMessage {
    private String to; // 수신자
    private String subject; // 제목
    private String message; // 내용
}