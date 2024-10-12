package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    // 북마크 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductDTO>> getBookmarksByUserId(@PathVariable("userId") Long userId,
                                                                 @RequestParam(name = "sort", defaultValue = "asc") String sort) {
        List<ProductDTO> bookmarks = bookmarkService.getBookmarks(userId, sort);
        if (bookmarks.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(bookmarks);
    }

    // 북마크 추가
    @PostMapping("/save")
    public ResponseEntity<Void> saveBookmark(@RequestParam("proId") Long proId,
                                             @RequestParam("userId") Long userId) {
        try {
            bookmarkService.saveBookmark(proId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 북마크 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBookmark(@RequestParam("proId") Long proId,
                                               @RequestParam("userId") Long userId) {
        try {
            bookmarkService.deleteBookmark(proId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}