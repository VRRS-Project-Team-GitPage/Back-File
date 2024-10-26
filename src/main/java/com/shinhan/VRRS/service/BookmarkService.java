package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.entity.Bookmark;
import com.shinhan.VRRS.entity.CompositePK;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.repository.BookmarkRepository;
import com.shinhan.VRRS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final ProductRepository productRepository;

    public boolean existsBookmark(Long proId, Long userId) {
        CompositePK id = new CompositePK(proId, userId);
        return bookmarkRepository.existsById(id);
    }

    // 북마크한 제품 목록 불러오기
    public List<ProductDTO> getBookmarks(Long userId) {
        // 북마크 조회 및 정렬
        List<Bookmark> bookmarks = bookmarkRepository.findByUserIdOrderByDateAsc(userId);

        // proId 추출
        List<Long> proIds = bookmarks.stream()
                .map(Bookmark::getProId)
                .collect(Collectors.toList());

        // 제품 조회
        List<Product> products = productRepository.findAllById(proIds);

        return products.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveBookmark(Long proId, Long userId) {
        Product product = productRepository.findById(proId).orElseThrow(NoSuchElementException::new);

        // 북마크 확인
        CompositePK id = new CompositePK(proId, userId);
        if (bookmarkRepository.existsById(id))
            throw new IllegalArgumentException();

        // 북마크 저장
        Bookmark bookmark = new Bookmark(proId, userId);
        bookmarkRepository.save(bookmark);

        // 북마크 수 증가 로직
        product.setBookmarkCnt(product.getBookmarkCnt() + 1);
        productRepository.save(product);
    }

    @Transactional
    public void deleteBookmark(Long proId, Long userId) {
        Product product = productRepository.findById(proId).orElseThrow(NoSuchElementException::new);

        // 북마크 확인
        CompositePK id = new CompositePK(proId, userId);
        if (!bookmarkRepository.existsById(id))
            throw new NoSuchElementException();

        // 북마크 삭제
        bookmarkRepository.deleteById(id);

        // 북마크 수 감소 로직
        product.setBookmarkCnt(Math.max(product.getBookmarkCnt() - 1, 0)); // 음수 방지
        productRepository.save(product);
    }
}