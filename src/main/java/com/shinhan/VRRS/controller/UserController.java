package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.UserService;
import com.shinhan.VRRS.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 비밀번호 재설정
    @PostMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestHeader("Authorization") String jwt,
                                                 @RequestParam("password") String password) {
        // 아이디 추출
        String jwtToken = jwt.substring(7); // "Bearer " 제거
        String username = jwtUtil.extractUsername(jwtToken);

        User user = userService.getUserByUsername(username);
        assert user != null;
        userService.updatePassword(user, password);
        return ResponseEntity.ok("Password changed successfully");
    }
}
