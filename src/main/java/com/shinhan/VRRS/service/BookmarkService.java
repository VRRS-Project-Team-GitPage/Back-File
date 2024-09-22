package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.Bookmark;
import com.shinhan.VRRS.entity.CompositePK;
import com.shinhan.VRRS.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public void saveBookmark(Long proId, Long userId) {
        Bookmark bookmark = new Bookmark(proId, userId);
        bookmarkRepository.save(bookmark);
    }

    public boolean existsBookmark(Long proId, Long userId) {
        CompositePK id = new CompositePK(proId, userId);
        return bookmarkRepository.existsById(id);
    }
}
