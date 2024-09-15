package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.LoginDTO;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody User user) throws Exception {
        userService.join(user);
        return ResponseEntity.ok("User registered successfully");
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
//        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
//        return ResponseEntity.ok("Login successful");
//    }
}
