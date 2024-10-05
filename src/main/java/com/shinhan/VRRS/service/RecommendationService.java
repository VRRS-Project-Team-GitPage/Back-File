package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.RankResponse;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ProductRepository productRepository;

    public RankResponse getRank(Integer vegTypeId) {
        Pageable pageable = PageRequest.of(0, 10);
        // 전체 채식 유형
        List<ProductDTO> totalRank = productRepository.findByTotalRank(pageable)
                .stream().map(ProductDTO::new).toList();

        // 특정 채식 유형
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i = 1; i<= vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리
        List<ProductDTO> vegTypeRank =  productRepository.findByVegTypeRank(vegTypeIds, pageable)
                .stream().map(ProductDTO::new).toList();

        return new RankResponse(totalRank, vegTypeRank);
    }

    public List<Product> recommendReadingBased(Integer vegTypeId, String ingredients) {
        List<Integer> vegTypeIds = new ArrayList<>();
        if (vegTypeId != 3) for (int i=1; i<=vegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리

        List<Product> allProducts = productRepository.findByCustomVegTypeId(vegTypeIds);
        Set<String> selectedIngredients = new HashSet<>(parseIngredients(ingredients));

        Map<Product, Double> productSimilarityMap = new HashMap<>();
        for (Product product : allProducts) {
            Set<String> productIngredients = new HashSet<>(parseIngredients(product.getIngredients()));

            // 교집합 계산
            Set<String> commonIngredients = new HashSet<>(productIngredients);
            commonIngredients.retainAll(selectedIngredients);
            int similarity = commonIngredients.size();

            // 일치 비율 계산 (일치하는 원재료 / 전체 원재료 수)
            double totalIngredients = Math.max(productIngredients.size(), selectedIngredients.size());  // 더 큰 값을 기준으로 비율 계산
            double similarityRatio = (double) similarity / totalIngredients;

            // 일정 비율 이상의 유사도만 추천
            if (similarityRatio >= 0.3) { // 30% 이상 일치하는 경우만 포함
                productSimilarityMap.put(product, similarityRatio);
            }
        }

        // 유사도에 따라 정렬 후 추천
        return productSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<String> parseIngredients(String ingredient) {
        List<String> parsedIngredients = new ArrayList<>();
        StringBuilder currentIngredient = new StringBuilder();
        Deque<Character> bracketStack = new ArrayDeque<>();

        for (char c : ingredient.toCharArray()) {
            if (c == '(' || c == '[') {
                if (bracketStack.isEmpty() && !currentIngredient.isEmpty()) {
                    // 괄호 시작 전에 있던 원재료 추가
                    parsedIngredients.add(currentIngredient.toString().trim());
                    currentIngredient.setLength(0);
                }
                bracketStack.push(c);
            } else if (c == ')' || c == ']') {
                bracketStack.pop();
                if (bracketStack.isEmpty()) {
                    // 괄호 안의 원재료를 개별 항목으로 분리
                    String[] innerIngredients = currentIngredient.toString().split(",");
                    for (String innerIngredient : innerIngredients) {
                        parsedIngredients.add(innerIngredient.trim());
                    }
                    currentIngredient.setLength(0); // 세부 원재료 처리 후 초기화
                }
            } else if (c == ',' && bracketStack.isEmpty()) {
                // 괄호 밖의 원재료 처리
                if (!currentIngredient.isEmpty()) {
                    parsedIngredients.add(currentIngredient.toString().trim());
                    currentIngredient.setLength(0);
                }
            } else {
                // 괄호 안이나 괄호 밖의 문자열을 추가
                currentIngredient.append(c);
            }
        }

        // 마지막 원재료 추가
        if (!currentIngredient.isEmpty())
            parsedIngredients.add(currentIngredient.toString().trim());
        return parsedIngredients;
    }
}
