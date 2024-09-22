package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean existsUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void setTempPassword(String to, String code) {
        User user = userRepository.findByEmail(to).orElseThrow(NullPointerException::new);
        user.setPassword(passwordEncoder.encode(code));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}

