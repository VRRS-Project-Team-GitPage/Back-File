package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.*;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.CustomUserDetails;
import com.shinhan.VRRS.service.EmailService;
import com.shinhan.VRRS.service.UserService;
import com.shinhan.VRRS.util.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailService mailService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody UserDTO userDto) {
        userService.join(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        final CustomUserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new LoginResponse(jwt, userDetails));
    }

    // 아이디 찾기
    @PostMapping("/find/username")
    public ResponseEntity<Map<String, String>> getUsername(@RequestBody UserInfoRequest request) {
        User user = userService.getUserByEmail(request.getUserInfo());
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        return ResponseEntity.ok(Map.of("username", user.getUsername()));
    }

    // 비밀번호 찾기
    @PostMapping("/find/password")
    public ResponseEntity<Void> sendPasswordMail(@RequestBody UserInfoRequest request) {
        User user = userService.getUserByEmail(request.getUserInfo());
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found

        EmailMessage emailMessage = EmailMessage.builder()
                                                .to(user.getEmail())
                                                .subject("[채식어디] 임시 비밀번호 발급")
                                                .build();

        try {
            String code = mailService.sendMail(emailMessage, "password");
            userService.setTempPassword(emailMessage.getTo(), code);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Server Error
        }
    }

    // 인증 메일 전송
    @PostMapping("/email")
    public ResponseEntity<AuthCode> sendJoinMail(@RequestBody UserInfoRequest request) {
        // 이메일 중복 확인
        if (userService.existsEmail(request.getUserInfo()))
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict

        EmailMessage emailMessage = EmailMessage.builder()
                                                .to(request.getUserInfo())
                                                .subject("[채식어디] 이메일 인증 코드 발송")
                                                .build();

        try {
            String code = mailService.sendMail(emailMessage, "email");
            return ResponseEntity.ok(new AuthCode(code));
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
    }

    // 아이디 중복 확인
    @PostMapping("/check/username")
    public ResponseEntity<Boolean> checkUsername(@RequestBody UserInfoRequest request) {
        return ResponseEntity.ok(userService.existsUsername(request.getUserInfo()));
    }
}