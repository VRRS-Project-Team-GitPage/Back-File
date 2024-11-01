package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.UserDTO;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import com.shinhan.VRRS.repository.*;
import com.shinhan.VRRS.util.HashUtil;
import com.shinhan.VRRS.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VegetarianTypeRepository vegTypeRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ProductRepository productRepository;

    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(HashUtil.hash(email));
    }

    public Boolean existsUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(HashUtil.hash(email))
                .orElseThrow(NoSuchElementException::new);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(NoSuchElementException::new);
    }

    // 토큰으로 사용자 조회
    public User getUserFromJwt(String jwt) {
        String jwtToken = jwt.substring(7); // 'Bearer ' 제거
        String userId = JwtUtil.extractUsername(jwtToken); // 아이디 추출
        return getUserByUsername(userId); // 사용자 정보 반환
    }

    @Transactional
    public void resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void join(UserDTO userDto) {
        // 이메일 및 아이디 중복 확인
        if (existsEmail(userDto.getEmail()) || existsUsername(userDto.getUsername()))
            throw new IllegalArgumentException();

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setEmail(HashUtil.hash(userDto.getEmail()));
        VegetarianType vegType = vegTypeRepository.findById(userDto.getVegTypeId()).orElse(null);
        userRepository.save(userDto.convertToEntity(vegType));
    }

    @Transactional
    public void deleteUserById(Long id) {
        List<Review> userReviews = reviewRepository.findByUserId(id);
        List<Long> userBookmarks = bookmarkRepository.findProIdsByUserId(id);

        // 추천수 감소
        for (Review review : userReviews) {
            Long proId = review.getProId();
            if (review.isRec()) productRepository.decrementRecCut(proId);
            else productRepository.decrementNotRecCnt(proId);
        }

        // 북마크수 감소
        for (Long proId : userBookmarks)
            productRepository.decrementBookmarkCnt(proId);

        userRepository.deleteById(id);
    }

    @Transactional
    public void updateNicknameAndVegType(User user, String nickname, Integer vegTypeId) {
        VegetarianType vegType = vegTypeRepository.findById(vegTypeId).orElse(null);

        // 닉네임 및 채식 유형 업데이트
        user.setNickname(nickname);
        user.setVegType(vegType);

        userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDate.now());
        userRepository.save(user);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new CustomUserDetails(user);
    }
}