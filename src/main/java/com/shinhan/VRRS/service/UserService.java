package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.UserDTO;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import com.shinhan.VRRS.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VegetarianTypeRepository vegTypeRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ProductRepository productRepository;

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
    public void setPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void setTempPassword(String to, String code) {
        User user = userRepository.findByEmail(to).orElseThrow(NullPointerException::new);
        user.setPassword(passwordEncoder.encode(code));
        userRepository.save(user);
    }

    @Transactional
    public void join(UserDTO userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        VegetarianType vegType = vegTypeRepository.findById(userDto.getVegTypeId()).orElse(null);
        userRepository.save(userDto.convertToEntity(vegType));
    }

    @Transactional
    public void deleteUserById(Long id) {
        // 리뷰수와 북마크수 감소
        List<Review> userReviews = reviewRepository.findByUserId(id);
        List<Long> userBookmarks = bookmarkRepository.findProIdsByUserId(id);

        for (Review review : userReviews) {
            Long proId = review.getProId();
            if (review.isRec()) productRepository.decrementRecCut(proId);
            else productRepository.decrementNotRecCnt(proId);

            if (review.getContent() != null) productRepository.decrementReviewCnt(proId);
        }

        for (Long proId : userBookmarks) productRepository.decrementBookmarkCnt(proId);

        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}