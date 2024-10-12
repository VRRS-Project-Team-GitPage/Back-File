package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String email; // 이메일
    private String username; // 아이디
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private int vegTypeId; // 채식유형

    public User convertToEntity(VegetarianType vegType) {
        return new User(email, username, password, nickname, vegType);
    }
}
