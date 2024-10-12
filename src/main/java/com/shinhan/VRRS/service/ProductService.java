package com.shinhan.VRRS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.cache.annotation.Cacheable;
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
        if (vegTypeId == 6) return productRepository.findAll(pageable).map(ProductDTO::new);

        // 특정 채식유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        return productRepository.findByCustomVegTypeId(vegTypeIds, pageable).map(ProductDTO::new);
    }

    public List<ProductDTO> getProducts(String name) {
        return productRepository.findByCustomNameContaining(name).stream().map(ProductDTO::new).toList();
    }

    public Product getProduct(String reportNum) {
        return productRepository.findByReportNum(reportNum).orElse(null);
    }

    @Transactional
    public void saveProduct(Product product, String imagePath) {
        product.setImgPath(imagePath); // 이미지 경로 설정
        productRepository.save(product);
    }

    public Product newProduct(String jsonData) throws JsonProcessingException {
        // JSON 문자열 -> Product 객체
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonData);

        String name = jsonNode.path("name").asText();
        String ingredients = jsonNode.path("ingredients").asText();
        String reportNum = jsonNode.path("reportNum").asText();
        Integer categoryId = jsonNode.path("categoryId").asInt();
        Integer vegTypeId = jsonNode.path("vegTypeId").asInt();

        // Product 객체 생성
        Product product = new Product();
        product.setName(name);
        product.setIngredients(ingredients);
        product.setReportNum(reportNum);

        // 제품 중복 확인
        if (productRepository.existsByReportNum(product.getReportNum())) return null;

        // 외래 키로 참조되는 엔티티 조회
        Category category = categoryRepository.findById(categoryId).orElse(null);
        VegetarianType vegType = vegTypeRepository.findById(vegTypeId).orElse(null);

        // 조회한 엔티티로 설정
        product.setCategory(category);
        product.setVegType(vegType);

        return product;
    }

    public ProductDetails newProductDetails(Long id) {
        return productRepository.findById(id).map(ProductDetails::new)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // 캐싱 적용 (자주 조회되는 데이터를 메모리에 저장하고 일정 시간 동안 캐시된 데이터를 반환함으로써 DB 요청을 줄일 수 있음)
    @Cacheable(value = "productReviewCounts", key = "#proId")
    public Product getProductReviewCounts(Long proId) {
        return productRepository.findById(proId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}