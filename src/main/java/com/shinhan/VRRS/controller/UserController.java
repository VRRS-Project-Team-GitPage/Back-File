package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.UserDTO;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 내 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(/*@RequestHeader("Authorization") String jwt,*/
                                           @Valid @RequestBody UserDTO request) {
        if (request.getNickname() == null || request.getVegTypeId() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

//        User user = userService.getUserFromJwt(jwt);
        User user = userService.getUserByUsername("veggielife");

        userService.updateNicknameAndVegType(user, request.getNickname(), request.getVegTypeId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    // 비밀번호 재설정
    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(/*@RequestHeader("Authorization") String jwt,*/
                                              @Valid @RequestBody UserDTO request) {
        if (request.getPassword() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

//        User user = userService.getUserFromJwt(jwt);
        User user = userService.getUserByUsername("veggielife");

        userService.resetPassword(user, request.getPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    // 회원탈퇴
    @DeleteMapping("/withdrawal")
    public ResponseEntity<Void> withdrawal(/*@RequestHeader("Authorization") String jwt*/) {
//        Long userId = userService.getUserFromJwt(jwt).getId();
        Long userId = userService.getUserByUsername("veggielife").getId();

        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}