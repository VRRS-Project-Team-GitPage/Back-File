package com.shinhan.VRRS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.VRRS.dto.ProductDetailsDTO;
import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void registerProduct(Product product, String imagePath) {
        product.setImgPath(imagePath); // 이미지 경로 설정
        productRepository.save(product);
    }

    public Slice<ProductDTO> getAllProduct(int vegTypeId, Pageable pageable) {
        // 전체 채식 유형
        if (vegTypeId == 7) return productRepository.findAll(pageable).map(this::convertToDTO);

        // 특정 채식 유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        return productRepository.findByCustomVegTypeId(vegTypeIds, pageable).map(this::convertToDTO);
    }

    public List<ProductDTO> searchProduct(String name) {
        return productRepository.findByNameContaining(name).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDetailsDTO getProductDetails(Long id) {
        return productRepository.findById(id).map(this::convertToDetailsDTO).orElse(null);
    }

    // isRec: rec 업데이트(true), notRec 업데이트(false)
    // isIncrement: 증가(true), 감소(flase)
    public void updateProductReview(Long id, boolean isRec, boolean isIncrement) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (isIncrement) {
            if (isRec) product.setRecCnt(product.getRecCnt() + 1);
            else product.setNotRecCnt(product.getNotRecCnt() + 1);
        }
        else { // 값이 0 미만으로 내려가지 않도록 설정
            if (isRec) product.setRecCnt(Math.max(product.getRecCnt() - 1, 0));
            else product.setNotRecCnt(Math.max(product.getNotRecCnt() - 1, 0));
        }

        productRepository.save(product);
    }

    public Product newProduct(String jsonData) throws JsonProcessingException {
        // JSON 문자열 -> Product 객체
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(jsonData, Product.class);

        // 제품명 중복 확인
        if (productRepository.existsByName(product.getName())) return null;
        return product;
    }

    public ProductDTO convertToDTO(Product product) {
        // 이미지 URL 생성
        String imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/images").path(product.getImgPath()).toUriString();
        return new ProductDTO(product, imgUrl);
    }

    public ProductDetailsDTO convertToDetailsDTO(Product product) {
        return new ProductDetailsDTO(product);
    }
}
