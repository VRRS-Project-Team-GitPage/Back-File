package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordFindRequest {
    private String code;
    private String username;

    public PasswordFindRequest(String code, String username) {
        this.code = code;
        this.username = username;
    }
}
