package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    public Long getId() {return user.getId();}

    public String getNickname() {return user.getNickname();}

    public VegetarianType getVegType() {return user.getVegType();}

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 비즈니스 로직에 맞게 설정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 상태 반환
    }
}