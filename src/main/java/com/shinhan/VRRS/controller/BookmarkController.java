package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.service.BookmarkService;
import com.shinhan.VRRS.service.UserService;
import jakarta.validation.constraints.Min;
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
    private final UserService userService;

    // 북마크 조회
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getBookmarksByUserId(@RequestHeader("Authorization") String jwt) {
        Long userId = userService.getUserFromJwt(jwt).getId();
        List<ProductDTO> bookmarks = bookmarkService.getBookmarks(userId);
        if (bookmarks.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(bookmarks);
    }

    // 북마크 추가
    @PostMapping("/insert")
    public ResponseEntity<Void> saveBookmark(@RequestHeader("Authorization") String jwt,
                                             @RequestParam("proId") @Min(1) Long proId) {
        try {
            Long userId = userService.getUserFromJwt(jwt).getId();
            bookmarkService.saveBookmark(proId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 북마크 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBookmark(@RequestHeader("Authorization") String jwt,
                                               @RequestParam("proId") @Min(1) Long proId) {
        try {
            Long userId = userService.getUserFromJwt(jwt).getId();
            bookmarkService.deleteBookmark(proId, userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}