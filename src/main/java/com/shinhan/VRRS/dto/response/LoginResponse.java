package com.shinhan.VRRS.dto.response;

import com.shinhan.VRRS.entity.VegetarianType;
import com.shinhan.VRRS.service.CustomUserDetails;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {
    private String jwt;
    private String nickname;
    private VegetarianType vegType;

    public LoginResponse(String jwt, CustomUserDetails user) {
        this.jwt = jwt;
        this.nickname = user.getNickname();
        this.vegType = user.getVegType();
    }
}