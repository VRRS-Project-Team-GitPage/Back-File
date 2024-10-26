package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.response.RankResponse;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.repository.RecommendationRepository;
import com.shinhan.VRRS.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final ReviewRepository reviewRepository;

    public RankResponse getRank(Integer vegTypeId) {
        Pageable pageable = PageRequest.of(0, 10);
        // 전체 채식 유형
        List<ProductDTO> totalRank = recommendationRepository.findByTotalRank(pageable)
                .stream().map(ProductDTO::new).toList();

        // 특정 채식 유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i = 1; i<= vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        List<ProductDTO> vegTypeRank =  recommendationRepository.findByVegTypeRank(vegTypeIds, pageable)
                .stream().map(ProductDTO::new).toList();

        return new RankResponse(totalRank, vegTypeRank);
    }

    // 카테고리 기반 추천
    public List<ProductDTO> recommendByCategory(Integer categoryId, User user) {
        Long userId = user.getId();
        Integer vegTypeId = user.getVegType().getId();

        // 채식유형 설정
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i = 1; i<= vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리

        // 추천 제품 및 리뷰 제품 ID 조회
        List<Product> recommendedProducts = reviewRepository.findLikedProductIdsByUserId(userId);
        List<Long> reviewProductIds = reviewRepository.findReviewProductIdsByUserId(userId);

        // 채식유형에 맞는 제품 검색 (리뷰 제품 제외)
        List<Product> categoryProducts = recommendationRepository.findByCategoryAndVegTypeInAndIdNotIn(categoryId, vegTypeIds, reviewProductIds);

        return sortBySimilarity(null, categoryProducts, recommendedProducts);
    }

    // 키워드 기반 추천
    public List<ProductDTO> recommendByKeyword(String keyword, User user) {
        Long userId = user.getId();
        Integer vegTypeId = user.getVegType().getId();

        // 채식유형 설정
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i = 1; i<= vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리

        // 추천 제품 및 리뷰 제품 ID 조회
        List<Product> recommendedProducts = reviewRepository.findLikedProductIdsByUserId(userId);
        List<Long> reviewProductIds = reviewRepository.findReviewProductIdsByUserId(userId);

        // 채식유형에 맞는 제품 검색 (리뷰 제품 제외)
        List<Product> keywordProducts = recommendationRepository.findByKeywordAndVegTypeInAndIdNotIn(keyword, vegTypeIds, reviewProductIds);

        return sortBySimilarity(keyword, keywordProducts, recommendedProducts);
    }

    // 판독 기반 추천
    public List<ProductDTO> recommendByReading(Integer vegTypeId, String ingredients) {
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리

        List<Product> allProducts = recommendationRepository.findByCustomVegTypeId(vegTypeIds);
        Set<String> selectedIngredients = parseIngredients(ingredients);

        Map<Product, Double> productSimilarityMap = new HashMap<>();
        for (Product product : allProducts) {
            Set<String> productIngredients = parseIngredients(product.getIngredients());

            // 교집합 계산
            double similarity = calculateJaccardSimilarity(productIngredients, selectedIngredients);

            // 30% 이상의 유사도만 추천
            if (similarity >= 0.3)
                productSimilarityMap.put(product, similarity);
        }

        // 유사도 기준으로 정렬
        return productSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // 유사도 정렬
    private List<ProductDTO> sortBySimilarity(String keyword, List<Product> products, List<Product> recommendedProducts) {
        Map<Product, Integer> similarityMap = new HashMap<>();

        for (Product product : products) {
            int similarity = calculateSimilarity(product, recommendedProducts);
            if (keyword != null && product.getName().contains(keyword))
                similarity += 10;
            similarityMap.put(product, similarity);
        }

        // 유사도 기준으로 정렬
        return products.stream()
                .sorted((p1, p2) -> Integer.compare(similarityMap.get(p2), similarityMap.get(p1)))
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // 부분 일치 유사도 계산
    private int calculateSimilarity(Product product, List<Product> recommendedProducts) {
        int similarityScore = 0;
        Set<String> productIngredients = parseIngredients(product.getIngredients());

        for (Product recommendedProduct : recommendedProducts) {
            Set<String> recommendedIngredients = parseIngredients(recommendedProduct.getIngredients());

            // 원재료가 겹치는 경우 점수 추가
            Set<String> commonIngredients = new HashSet<>(productIngredients);
            commonIngredients.retainAll(recommendedIngredients);
            similarityScore += commonIngredients.size();
        }
        return similarityScore;
    }

    // 자카르타 유사도 계산
    private double calculateJaccardSimilarity(Set<String> productIngredients, Set<String> selectedIngredients) {
        // 교집합: 두 제품 간의 공통 원재료
        Set<String> intersection = new HashSet<>(productIngredients);
        intersection.retainAll(selectedIngredients);

        // 합집합: 두 제품의 모든 원재료
        Set<String> union = new HashSet<>(productIngredients);
        union.addAll(selectedIngredients);

        // 자카르타 유사도 계산
        return (double) intersection.size() / union.size();
    }

    // 원재료 분리
    public Set<String> parseIngredients(String ingredient) {
        Set<String> parsedIngredients = new LinkedHashSet<>();
        StringBuilder currentIngredient = new StringBuilder();

        for (char c : ingredient.toCharArray()) {
            if (c == ',' || c == '(' || c == '[' || c == ')' || c == ']') {
                // 현재 원재료가 비어있지 않으면 추가
                if (!currentIngredient.isEmpty()) {
                    parsedIngredients.add(currentIngredient.toString().trim());
                    currentIngredient.setLength(0); // 원재료 초기화
                }
            } else {
                // 원재료의 문자 추가
                currentIngredient.append(c);
            }
        }

        // 마지막 원재료 추가
        if (!currentIngredient.isEmpty())
            parsedIngredients.add(currentIngredient.toString().trim());

        return parsedIngredients;
    }
}
