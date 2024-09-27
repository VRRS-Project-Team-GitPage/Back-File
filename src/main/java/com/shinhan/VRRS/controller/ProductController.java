package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductDetails;
import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.service.BookmarkService;
import com.shinhan.VRRS.service.ImageService;
import com.shinhan.VRRS.service.ProductService;
import com.shinhan.VRRS.service.ReviewDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageService productImageService;
    private final BookmarkService bookmarkService;
    private final ReviewDetailsService reviewDetailsService;

    // 모든 제품 조회
    @GetMapping
    public ResponseEntity<Slice<ProductDTO>> getAllProduct(@RequestParam(name = "vegTypeId", defaultValue = "7") Integer vegTypeId,
                                                           Pageable pageable) {
        Slice<ProductDTO> products = productService.getAllProduct(vegTypeId, pageable);
        return ResponseEntity.ok(products);
    }

    // 제품 조회
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> getProduct(@RequestParam("name") String name) {
        List<ProductDTO> products = productService.getProduct(name);
        if (products.isEmpty())
            return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(products);
    }

    // 제품 상세 조회
    @GetMapping("/{proId}")
    public ResponseEntity<ProductDetails> getProductDetails(@PathVariable("proId") Long proId,
                                                            @RequestParam("userId") Long userId) {
        ProductDetails productDetails = productService.newProductDetails(proId);
        productDetails.setBookmark(bookmarkService.existsBookmark(proId, userId));
        productDetails.setUserReview(reviewDetailsService.getReview(proId, userId));
        productDetails.setReviews(reviewDetailsService.getPreviewReview(proId, userId));
        return ResponseEntity.ok(productDetails);
    }

    // 제품 등록
    @PostMapping("/new")
    public ResponseEntity<String> saveProduct(@RequestParam("image") MultipartFile image,
                                              @RequestParam("jsonData") String jsonData) {
        try {
            Product product = productService.newProduct(jsonData);
            if (product == null)
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
            String imgPath = productImageService.uploadProductImage(image);
            productService.saveProduct(product, imgPath);
            return ResponseEntity.ok("Registration complete");
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
    }

    // 제품 이미지 URL 처리
    @GetMapping("/images/{imgPath}")
    public ResponseEntity<Resource> getProductImage(@PathVariable("imgPath") String imgPath) {
        try {
            Resource resource = productImageService.getImage(imgPath);
            if (!resource.exists() || !resource.isReadable())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            // MediaType mediaType = MediaType.parseMediaType("image/webp");
            return ResponseEntity.ok().contentType(MediaType.ALL).body(resource);
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}