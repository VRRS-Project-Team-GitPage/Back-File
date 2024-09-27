package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/save")
    public String saveBookmark(@RequestParam("proId") Long proId,
                               @RequestParam("userId") Long userId) {
        bookmarkService.saveBookmark(proId, userId);
        return "Bookmark saved successfully!";
    }
}