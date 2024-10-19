package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {
    @Email
    private String email; // 이메일
    @Size(min = 6, max = 12)
    private String username; // 아이디
    @Pattern(regexp = "^(?=(.*[A-Za-z].*)|(.*\\d.*)|(.*[!@#$%^&*].*)){2}[A-Za-z\\d!@#$%^&*]{8,20}$")
    private String password; // 비밀번호
    @Size(min = 2, max = 15)
    private String nickname; // 닉네임
    @Min(1) @Max(6)
    private Integer vegTypeId; // 채식유형

    public User convertToEntity(VegetarianType vegType) {
        return new User(email, username, password, nickname, vegType);
    }
}