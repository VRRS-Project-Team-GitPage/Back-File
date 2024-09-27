package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.*;
import com.shinhan.VRRS.entity.User;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final EmailService mailService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserDTO userDto) {
        userService.join(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Join successful"); // 201 Created
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        final User user = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(new LoginResponse(jwt, user));
    }

    // 아이디 찾기
    @PostMapping("/find/username")
    public ResponseEntity<UsernameResponse> getUsername(@RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        return ResponseEntity.ok(new UsernameResponse(user.getUsername()));
    }

    // 비밀번호 찾기
    @PostMapping("/find/password")
    public ResponseEntity<String> sendPasswordMail(@RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found

        EmailMessage emailMessage = EmailMessage.builder()
                                                .to(user.getEmail())
                                                .subject("[채식어디] 임시 비밀번호 발급")
                                                .build();

        try {
            String code = mailService.sendMail(emailMessage, "password");
            userService.setTempPassword(emailMessage.getTo(), code);
            return ResponseEntity.ok("Send temporary password");
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Server Error
        }
    }

    // 인증 메일 전송
    @PostMapping("/email")
    public ResponseEntity<AuthCode> sendJoinMail(@RequestParam("email") String email) {
        EmailMessage emailMessage = EmailMessage.builder()
                                                .to(email)
                                                .subject("[채식어디] 이메일 인증 코드 발송")
                                                .build();

        try {
            String code = mailService.sendMail(emailMessage, "email");
            return ResponseEntity.ok(new AuthCode(code));
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
    }

    // 이메일 중복 확인
    @PostMapping("/check/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(userService.existsEmail(email));
    }

    // 아이디 중복 확인
    @PostMapping("/check/username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(userService.existsUsername(username));
    }
}