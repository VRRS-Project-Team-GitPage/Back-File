package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import com.shinhan.VRRS.service.CustomUserDetails;
import lombok.Getter;
import lombok.Locked;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
public class LoginResponse {
    private String jwt;
    private Long userId;
    private String nickname;
    private VegetarianType vegType;

    public LoginResponse(String jwt, CustomUserDetails user) {
        this.jwt = jwt;
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.vegType = user.getVegType();
    }
}