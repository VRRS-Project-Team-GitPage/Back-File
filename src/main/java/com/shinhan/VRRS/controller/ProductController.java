package com.shinhan.VRRS.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.service.ProductImageService;
import com.shinhan.VRRS.service.ProductService;
import com.shinhan.VRRS.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    // 제품 등록
    @PostMapping("/dictionary/new")
    public ResponseEntity<String> insertProduct(@RequestParam("image") MultipartFile image,
                                                @RequestParam("jsonData") String jsonData) {
        try {
            Product product = productService.newProduct(jsonData);
            if (product == null) return ResponseEntity.status(204).body("이미 등록된 제품입니다.");

            String imgPath = productImageService.loadProductImage(image);
            productService.insertProduct(product, imgPath);
            return ResponseEntity.ok("제품 등록이 완료되었습니다.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).body("json 처리 과정에서 오류가 발생했습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(400).body("이미지 처리 과정에서 오류가 발생했습니다.");
        }
    }

    // 모든 제품 조회
    @GetMapping("/dictionary")
    public Slice<ProductDTO> getAllProduct(@RequestParam(name = "vegtype", defaultValue = "0") int vegType,
                                                         Pageable pageable) {
        return productService.getAllProduct(vegType, pageable);
    }
    // 제품 조회
    @GetMapping("/dictionary/search")
    public List<ProductDTO> getProducts(@RequestParam("name") String name) {
        return productService.getProducts(name);
    }

    // 추천수 또는 비추천수 수정 (PATCH)
    @PatchMapping("/dictionary/{id}/vote")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestParam boolean isRec, @RequestParam boolean isIncrement) {
        Product updatedProduct = productService.updateProductReview(id, isRec, isIncrement);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/images/{imgpath}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String imgPath) {
        try {
            Resource resource = productImageService.getProductImage(imgPath);
            if (!resource.exists() || !resource.isReadable())
                throw new FileNotFoundException("파일을 찾을 수 없습니다. " + imgPath);
            // MediaType mediaType = MediaType.parseMediaType("image/webp");
            return ResponseEntity.ok()
                    .contentType(MediaType.ALL)  // 이미지 타입에 맞게 설정
                    .body(resource);
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}