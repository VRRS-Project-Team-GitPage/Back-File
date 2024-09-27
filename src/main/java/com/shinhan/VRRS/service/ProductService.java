package com.shinhan.VRRS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.VRRS.dto.ProductDetails;
import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.entity.Category;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.VegetarianType;
import com.shinhan.VRRS.repository.CategoryRepository;
import com.shinhan.VRRS.repository.ProductRepository;
import com.shinhan.VRRS.repository.VegetarianTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VegetarianTypeRepository vegTypeRepository;

    public Slice<ProductDTO> getAllProduct(Integer vegTypeId, Pageable pageable) {
        // 전체 채식유형
        if (vegTypeId == 7) return productRepository.findAll(pageable).map(ProductDTO::new);

        // 특정 채식유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        return productRepository.findByCustomVegTypeId(vegTypeIds, pageable).map(ProductDTO::new);
    }

    public List<ProductDTO> getProduct(String name) {
        return productRepository.findByCustomNameContaining(name).stream().map(ProductDTO::new).toList();
    }

    @Transactional
    public void saveProduct(Product product, String imagePath) {
        product.setImgPath(imagePath); // 이미지 경로 설정
        productRepository.save(product);
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

        // 외래 키로 참조되는 엔티티 조회
        Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
        VegetarianType vegType = vegTypeRepository.findById(product.getCategory().getId()).orElse(null);

        // 조회한 엔티티로 설정
        product.setCategory(category);
        product.setVegType(vegType);

        return product;
    }

    public ProductDetails newProductDetails(Long id) {
        return productRepository.findById(id).map(ProductDetails::new).orElse(null);
    }
}