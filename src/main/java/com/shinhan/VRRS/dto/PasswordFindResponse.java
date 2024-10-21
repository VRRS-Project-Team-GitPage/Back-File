package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordFindResponse {
    private String code;
    private String username;

    public PasswordFindResponse(String code, String username) {
        this.code = code;
        this.username = username;
    }
}
