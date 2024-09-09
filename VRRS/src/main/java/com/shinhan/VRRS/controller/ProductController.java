package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.service.ProductService;
import com.shinhan.VRRS.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    // 제품 등록 (POST)
//    public ResponseEntity<Product> insertProduct(@RequestBody Product product) {
//        Product insertProduct = productService.insertProduct(product);
//        return ResponseEntity.ok(insertProduct);
//    }
    @PostMapping("/product/new")
    public ResponseEntity<Boolean> insertProduct(@RequestBody Product product) {
        boolean isInsert = productService.insertProduct(product);
        return ResponseEntity.ok(isInsert);
    }

    // 모든 제품 조회
    @GetMapping("/products")
    public List<Product> getAllProduct() {
        return productService.getAllProduct();
    }

    // 제품 조회 (GET)
    @GetMapping("/product/{id}")
    public Optional<Product> getProduct(@PathVariable("id") Long id) {
        return productService.getProduct(id);
    }

    // 제품 조회 (GET)
    @GetMapping("/product")
    public List<Product> getProductByName(@RequestParam("name") String name) {
        return productService.getProductByName(name);
    }

    // 추천수 또는 비추천수 수정 (PATCH)
    @PatchMapping("/product/{id}/vote")
    public ResponseEntity<Product> updateProductRecs(@PathVariable Long id, @RequestParam boolean isRec, @RequestParam boolean isIncrement) {
        Product updatedProduct = productService.updateProductReview(id, isRec, isIncrement);
        return ResponseEntity.ok(updatedProduct);
    }
}

