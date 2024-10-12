package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.UpdateUserRequest;
import com.shinhan.VRRS.dto.UserInfoRequest;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 사용자 정보 수정
    @PutMapping("/update/info")
    public ResponseEntity<Void> updateUser(@RequestHeader("Authorization") String jwt,
                                           @RequestBody UpdateUserRequest request) {
        User user = userService.getUserFromJwt(jwt); // 현재 사용자 정보

        // 닉네임 및 채식유형 수정
        userService.updateNicknameAndVegType(user, request.getNickname(), request.getVegTypeId());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 비밀번호 재설정
    @PutMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String jwt,
                                               @RequestBody UserInfoRequest request) {
        User user = userService.getUserFromJwt(jwt); // 현재 사용자 정보

        // 비밀번호 재설정
        userService.setPassword(user, request.getUserInfo());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 회원탈퇴
    @DeleteMapping("/withdrawal")
    public ResponseEntity<Void> withdrawal(@RequestHeader("Authorization") String jwt) {
        User user = userService.getUserFromJwt(jwt); // 현재 사용자 정보

        // 사용자 삭제
        userService.deleteUserById(user.getId());
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}