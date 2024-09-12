package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 모든 사용자 정보 출력
    @GetMapping("/users")
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    // 이름으로 사용자 정보 검색
    @GetMapping("/user")
    public Optional<User> getUserByName(@RequestParam("name") String name) {
        return userService.getUserByName(name);
    }
}
