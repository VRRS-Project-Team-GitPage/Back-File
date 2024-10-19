package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{imgPath}")
    public ResponseEntity<Resource> getProductImage(@PathVariable("imgPath") String imgPath) {
        try {
            Resource resource = imageService.getImage(imgPath);
            if (!resource.exists() || !resource.isReadable())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            MediaType mediaType = MediaType.parseMediaType("image/webp");
            return ResponseEntity.ok().contentType(mediaType).body(resource);
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}
