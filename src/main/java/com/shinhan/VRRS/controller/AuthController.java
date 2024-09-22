package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.AuthCodeDTO;
import com.shinhan.VRRS.dto.AuthRequestDTO;
import com.shinhan.VRRS.dto.AuthResponseDTO;
import com.shinhan.VRRS.dto.EmailDTO;
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
    public ResponseEntity<String> join(@RequestBody User user) {
        userService.join(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Join successful"); // 201 Created
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO login) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // 401 Unauthorized
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(login.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
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

    // 아이디 찾기
    @PostMapping("/find/username")
    public ResponseEntity<String> getUsername(@RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        return ResponseEntity.ok(user.getUsername());
    }

    // 비밀번호 찾기
    @PostMapping("/find/password")
    public ResponseEntity<String> sendPasswordMail(@RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found

        EmailDTO emailDTO = EmailDTO.builder()
                                    .to(user.getEmail())
                                    .subject("[채식어디] 임시 비밀번호 발급")
                                    .build();
        try {
            String code = mailService.sendMail(emailDTO, "password");
            userService.setTempPassword(emailDTO.getTo(), code);
            return ResponseEntity.ok("Send temporary password");
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Server Error
        }
    }

    // 인증 메일 전송
    @PostMapping("/email")
    public ResponseEntity<AuthCodeDTO> sendJoinMail(@RequestParam("email") String email) {
        EmailDTO emailDTO = EmailDTO.builder()
                                    .to(email)
                                    .subject("[채식어디] 이메일 인증 코드 발송")
                                    .build();
        String code = null;
        try {
            code = mailService.sendMail(emailDTO, "email");
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
        return ResponseEntity.ok(new AuthCodeDTO(code));
    }
}
