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

    @Transactional
    public void saveBookmark(Long proId, Long userId) {
        CompositePK id = new CompositePK(proId, userId);
        if (!bookmarkRepository.existsById(id)) {
            Bookmark bookmark = new Bookmark(proId, userId);
            bookmarkRepository.save(bookmark);

            // 북마크 수 증가 로직
            Product product = productRepository.findById(proId).orElseThrow(NullPointerException::new);
            product.setBookmarkCnt(product.getBookmarkCnt() + 1);
            productRepository.save(product);
        } else {
            throw new RuntimeException("Bookmark already exists.");
        }
    }

    @Transactional
    public void deleteBookmark(Long proId, Long userId) {
        CompositePK id = new CompositePK(proId, userId);
        if (bookmarkRepository.existsById(id)) {
            bookmarkRepository.deleteById(id);

            // 북마크 수 감소 로직
            Product product = productRepository.findById(proId).orElseThrow(NullPointerException::new);
            product.setBookmarkCnt(Math.max(product.getBookmarkCnt() - 1, 0)); // 음수 방지
            productRepository.save(product);
        } else {
            throw new RuntimeException("Bookmark not found.");
        }
    }

    // 북마크한 제품 목록 불러오기
    public List<ProductDTO> getBookmarks(Long userId, String sortOrder) {
        Sort sort = Sort.by("date");
        if ("desc".equalsIgnoreCase(sortOrder))
            sort = sort.descending();
        else
            sort = sort.ascending();

        // Bookmark에서 userId로 필터링하고 date로 정렬
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId, sort);

        // Bookmark에서 productId 추출
        List<Long> productIds = bookmarks.stream()
                .map(Bookmark::getProId)
                .collect(Collectors.toList());

        // Product 테이블에서 productIds로 조회
        List<Product> products = productRepository.findAllById(productIds);

        // Product를 ProductDTO로 변환
        return products.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }
}