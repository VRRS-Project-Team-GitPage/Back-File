package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.*;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.CustomUserDetails;
import com.shinhan.VRRS.service.EmailService;
import com.shinhan.VRRS.service.UserService;
import com.shinhan.VRRS.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
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
    private final UserService userService;
    private final EmailService mailService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<Void> join(@Valid @RequestBody UserDTO request) {
        if (request.getEmail() == null || request.getUsername() == null || request.getPassword() == null ||
                request.getNickname() == null || request.getVegTypeId() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        userService.join(request);
        return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        final CustomUserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        final String jwt = JwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new LoginResponse(jwt, userDetails));
    }

    // 아이디 찾기
    @PostMapping("/find/username")
    public ResponseEntity<Map<String, String>> getUsername(@Valid @RequestBody UserDTO request) {
        if (request.getEmail() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        User user = userService.getUserByEmail(request.getEmail());
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        return ResponseEntity.ok(Map.of("username", user.getUsername()));
    }

    // 비밀번호 찾기
    @PostMapping("/find/password")
    public ResponseEntity<PasswordFindResponse> sendPasswordMail(@Valid @RequestBody UserDTO request) {
        if (request.getEmail() == null || request.getUsername() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        User user = userService.getUserByEmail(request.getEmail());
        String username = request.getUsername();

        if (user == null || !user.getUsername().equals(username))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found

        EmailMessage emailMessage = EmailMessage.builder()
                                                .to(user.getEmail())
                                                .subject("[채식어디] 비밀번호 재설정 코드 발송")
                                                .build();

        try {
            String code = mailService.sendMail(emailMessage, "password");
            return ResponseEntity.ok(new PasswordFindResponse(code, username));
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Server Error
        }
    }

    // 비밀번호 재설정
    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody LoginRequest request) {
        User user = userService.getUserByUsername(request.getUsername()); // 현재 사용자 정보

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // 비밀번호 재설정
        userService.setPassword(user, request.getPassword());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 인증 메일 전송
    @PostMapping("/auth-email")
    public ResponseEntity<Map<String, String>> sendJoinMail(@Valid @RequestBody UserDTO request) {
        if (request.getEmail() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        // 이메일 중복 확인
        if (userService.existsEmail(request.getEmail()))
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict

        EmailMessage emailMessage = EmailMessage.builder()
                                                .to(request.getEmail())
                                                .subject("[채식어디] 이메일 인증 코드 발송")
                                                .build();

        try {
            String code = mailService.sendMail(emailMessage, "email");
            return ResponseEntity.ok(Map.of("code", code));
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
    }

    // 아이디 중복 확인
    @PostMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@Valid @RequestBody UserDTO request) {
        if (request.getUsername() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        Boolean exists = userService.existsUsername(request.getUsername());
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}