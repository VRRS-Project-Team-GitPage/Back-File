package com.shinhan.VRRS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email; // 이메일
    private String username; // 아이디
    private String password; // 비밀번호
    private String nickname; // 닉네임

    @ManyToOne
    @JoinColumn(name = "veg_type_id")
    private VegetarianType vegType; // 채식 유형

    public User(String email, String username, String password, String nickname, VegetarianType vegType) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.vegType = vegType;
    }
}