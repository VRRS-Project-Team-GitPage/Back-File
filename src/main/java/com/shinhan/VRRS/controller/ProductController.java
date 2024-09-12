package com.shinhan.VRRS.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shinhan.VRRS.service.productService.ProductImageService;
import com.shinhan.VRRS.service.productService.ProductService;
import com.shinhan.VRRS.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    // 제품 등록 (POST)
    @PostMapping("/product/new")
    public ResponseEntity<String> registerProduct(@RequestParam("image") MultipartFile image,
                                                  @RequestParam("jsonData") String jsonData) {
        try {
            // JSON 문자열 -> Product 객체
            Product product = productService.newProduct(jsonData);

            if (!productService.checkName(product.getName())) {
                String dbFilePath = productImageService.saveProductImage(image);
                productService.insertProduct(product, dbFilePath);
                return ResponseEntity.ok("제품 등록이 완료되었습니다.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).body("json 처리 과정에서 오류가 발생했습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(400).body("이미지 처리 과정에서 오류가 발생했습니다.");
        }
        return ResponseEntity.status(204).body("이미 등록된 제품입니다.");
    }

    // 모든 제품 조회
    @GetMapping("/products")
    public List<Product> getAllProduct() {
        return productService.getAllProduct();
    }

    // 제품 조회 (GET)
    @GetMapping("/product/{name}")
    public Optional<Product> getProduct(@PathVariable("id") Long id) {
        return productService.getProduct(id);
    }

    // 제품 조회 (GET)
    @GetMapping("/product")
    public List<Product> getProducts(@RequestParam("name") String name) {
        return productService.getProducts(name);
    }

    // 추천수 또는 비추천수 수정 (PATCH)
    @PatchMapping("/product/{id}/vote")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestParam boolean isRec, @RequestParam boolean isIncrement) {
        Product updatedProduct = productService.updateProductReview(id, isRec, isIncrement);
        return ResponseEntity.ok(updatedProduct);
    }
}

