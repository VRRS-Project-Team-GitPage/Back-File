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
import org.springframework.data.domain.Sort;
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

    public List<ProductDTO> getAllProduct(Integer vegTypeId, String sortOrder) {
        Sort sort = Sort.by("id");
        if ("recCnt".equals(sortOrder))
            sort = Sort.by("recCnt").descending();

        // 전체 채식유형
        if (vegTypeId == 6) return productRepository.findAll(sort).stream().map(ProductDTO::new).toList();

        // 특정 채식유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        return productRepository.findByCustomVegTypeId(vegTypeIds, sort).stream().map(ProductDTO::new).toList();
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

        // 필수 필드 검증
        if (name == null || ingredients == null || categoryId < 1 || categoryId > 4 || vegTypeId < 1 || vegTypeId > 6)
            throw new IllegalArgumentException();

        // Product 객체 생성
        Product product = new Product();
        product.setName(name);
        product.setIngredients(ingredients);
        product.setReportNum(reportNum);

        // 제품 중복 확인
        if (product.getReportNum() != null)
            if (productRepository.existsByReportNum(product.getReportNum())) return null;
        else
            if (productRepository.existsByName(product.getName())) return null; // 수입 제품 처리

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
}