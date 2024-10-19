package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDetails;
import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.service.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;
    private final BookmarkService bookmarkService;
    private final ReviewService reviewService;
    private final UserService userService;

    // 모든 제품 조회
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProduct(@RequestParam(name = "vegTypeId", defaultValue = "6")
                                                          @Min(1) @Max(6) Integer vegTypeId,
                                                          @RequestParam(name="sort", defaultValue = "id") String sort) {
        if (!sort.equals("id") && !sort.equals("recCnt"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        List<ProductDTO> products = productService.getAllProduct(vegTypeId, sort);
        return ResponseEntity.ok(products);
    }

    // 제품 조회
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> getProducts(@RequestParam("name") String name) {
        List<ProductDTO> products = productService.getProducts(name);
        if (products.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(products);
    }

    // 제품 상세 조회
    @GetMapping("/{proId}")
    public ResponseEntity<ProductDetails> getProductDetails(@RequestHeader("Authorization") String jwt,
                                                            @PathVariable("proId") @Min(1) Long proId) {
        try {
            Long userId = userService.getUserFromJwt(jwt).getId();
            ProductDetails productDetails = productService.newProductDetails(proId);
            productDetails.setBookmark(bookmarkService.existsBookmark(proId, userId));
            productDetails.setReviews(reviewService.getPreviewReview(proId, userId));
            return ResponseEntity.ok(productDetails);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 제품 등록
    @PostMapping("/submit")
    public ResponseEntity<Void> saveProduct(@RequestParam("image") MultipartFile image,
                                            @RequestParam("jsonData") String jsonData) {
        try {
            Product product = productService.newProduct(jsonData);
            if (product == null)
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
            String imgPath = imageService.uploadProductImage(image);
            productService.saveProduct(product, imgPath);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
    }
}