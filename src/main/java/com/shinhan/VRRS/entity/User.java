package com.shinhan.VRRS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider; // 로그인 제공자
    private String providerId; // 로그인 제공자 ID (API 로그인)
    private String name; // 사용자명
    private String email; // 이메일
    private String password; // 비밀번호 (자체 로그인)


    private int vegTypeId; // 채식 유형
}
