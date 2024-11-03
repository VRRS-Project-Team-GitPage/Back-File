package com.shinhan.VRRS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.VRRS.dto.response.ProductDetailsResponse;
import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.entity.Category;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.VegetarianType;
import com.shinhan.VRRS.repository.CategoryRepository;
import com.shinhan.VRRS.repository.ProductRepository;
import com.shinhan.VRRS.repository.VegetarianTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VegetarianTypeRepository vegTypeRepository;

    public List<ProductDTO> getAllProduct(Integer vegTypeId, String sort) {
        // 전체 채식유형
        if (vegTypeId == 6) {
            if (sort.equals("id"))
                return productRepository.findAll().stream().map(ProductDTO::new).toList();
            return productRepository.findAllOrderByPopularity().stream().map(ProductDTO::new).toList();
        }

        // 특정 채식유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리

        if (sort.equals("id"))
            return productRepository.findByVegTypeIds(vegTypeIds).stream().map(ProductDTO::new).toList();
        return productRepository.findByVegTypeIdsOrderByPopularity(vegTypeIds).stream().map(ProductDTO::new).toList();
    }

    public List<ProductDTO> getProducts(String name) {
        return productRepository.findByCustomNameContaining(name).stream().map(ProductDTO::new).toList();
    }

    public Product getProductNotThrows(String reportNum) {
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

        // 필수 필드 검증
        if (jsonNode.path("name").isMissingNode() || jsonNode.path("ingredients").isMissingNode() ||
            jsonNode.path("categoryId").isMissingNode() || jsonNode.path("vegTypeId").isMissingNode()) {
            throw new IllegalArgumentException();
        }

        // 필드 값 추출
        String name = jsonNode.path("name").asText();
        String ingredients = jsonNode.path("ingredients").asText();
        String reportNum = jsonNode.path("reportNum").asText("");
        Integer categoryId = jsonNode.path("categoryId").asInt();
        Integer vegTypeId = jsonNode.path("vegTypeId").asInt();

        // 추가 유효성 검사
        if (categoryId < 1 || categoryId > 4 || vegTypeId < 1 || vegTypeId > 6)
            throw new IllegalArgumentException();

        // 빈 품목보고번호 -> null
        if (reportNum.isEmpty())
            reportNum = null;

        // 제품 중복 확인
        if (reportNum != null && productRepository.existsByReportNum(reportNum)) return null;

        // Product 객체 생성
        Product product = new Product();
        product.setName(name);
        product.setIngredients(ingredients);
        product.setReportNum(reportNum);

        // 카테고리 및 채식 유형 조회
        Category category = categoryRepository.findById(categoryId).orElse(null);
        VegetarianType vegType = vegTypeRepository.findById(vegTypeId).orElse(null);

        // 카테고리 및 채식 유형 설정
        product.setCategory(category);
        product.setVegType(vegType);

        return product;
    }

    public ProductDetailsResponse newProductDetails(Long id) {
        return productRepository.findById(id)
                .map(ProductDetailsResponse::new)
                .orElseThrow(NoSuchElementException::new);
    }
}