package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.response.IngredientResponse;
import com.shinhan.VRRS.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReadingService {
    private final IngredientRepository ingredientRepository;

    public IngredientResponse checkConsumable(List<String> ingredients, Integer userVegTypeId, Integer proVegTypeId) {
        List<Integer> vegTypeIds = new ArrayList<>();
        if (userVegTypeId != 3) for (int i=1; i<=userVegTypeId; i++) vegTypeIds.add(i);
        else vegTypeIds = Arrays.asList(1, 3); // 오보 베지테리언 처리

        Integer finalVegTypeId = null;
        boolean hasNull = false;
        boolean isStart = true;

        List<String> consumables = new ArrayList<>();
        List<String> nonConsumables = new ArrayList<>();
        List<String> unidentifiables = new ArrayList<>();

        if (proVegTypeId != null && vegTypeIds.contains(proVegTypeId)) {
            consumables.addAll(ingredients);
            return new IngredientResponse(null, null, consumables, nonConsumables, unidentifiables);
        }

        for (String ingredient : ingredients) {
            // 괄호 밖의 원재료 먼저 처리
            String mainIngredient = getMainIngredient(ingredient);
            List<Integer> vegTypes = ingredientRepository.findVegTypeByIngredientName(mainIngredient);
            Integer vegType = vegTypes.isEmpty() ? null : vegTypes.get(0);

            if (vegType == null && isBracketContains(ingredient)) {
                // 괄호 안 원재료 확인
                List<String> subIngredients = extractBracketContent(ingredient);
                boolean isSubStart = true;

                for (String subIngredient : subIngredients) {
                    // 괄호 밖의 원재료 먼저 처리
                    String subMainIngredient = getMainIngredient(subIngredient);
                    List<Integer> subVegTypes = ingredientRepository.findVegTypeByIngredientName(subMainIngredient);
                    Integer subVegType = subVegTypes.isEmpty() ? null : subVegTypes.get(0);

                    if (subVegType == null && isBracketContains(subIngredient)) {
                        // 괄호 안 세부 원재료가 있는 경우 처리
                        List<String> detailedIngredients = extractBracketContent(subIngredient);
                        boolean isDetailStart = true;

                        for (String detailedIngredient : detailedIngredients) {
                            List<Integer> detailedVegTypes = ingredientRepository.findVegTypeByIngredientName(detailedIngredient);
                            Integer detailedVegType = detailedVegTypes.isEmpty() ? null : detailedVegTypes.get(0);

                            if (detailedVegType != null && detailedVegType == 7) {
                                subVegType = 7;
                                break;
                            }

                            if (subVegType == null) {
                                if (isDetailStart) {
                                    subVegType = detailedVegType;
                                    isDetailStart = false;
                                } else if (!vegTypeIds.contains(detailedVegType)) {
                                    subVegType = detailedVegType;
                                    hasNull = true;
                                }
                            } else if (detailedVegType == null) {
                                if (vegTypeIds.contains(subVegType))
                                    subVegType = null;
                                hasNull = true;
                            } else if ((detailedVegType == 2 && subVegType == 3) || (detailedVegType == 3 && subVegType == 2)) {
                                subVegType = 4;
                            } else {
                                subVegType = Math.max(subVegType, detailedVegType);
                            }
                        }
                    }

                    if (subVegType != null && subVegType == 7) {
                        vegType = 7;
                        break;
                    }

                    if (vegType == null) {
                        if (isSubStart) {
                            vegType = subVegType;
                            isSubStart = false;
                        } else if (!vegTypeIds.contains(subVegType)) {
                            vegType = subVegType;
                            hasNull = true;
                        }
                    } else if (subVegType == null) {
                        if (vegTypeIds.contains(vegType))
                            vegType = null;
                        hasNull = true;
                    } else if ((subVegType == 2 && vegType == 3) || (subVegType == 3 && vegType == 2)) {
                        vegType = 4;
                    } else {
                        vegType = Math.max(vegType, subVegType);
                    }
                }
            }

            // 리스트 삽입
            if (vegType == null)
                unidentifiables.add(ingredient);
            else if (vegTypeIds.contains(vegType))
                consumables.add(ingredient);
            else
                nonConsumables.add(ingredient);

            // 최종 채식유형 갱신
            if (finalVegTypeId != null && finalVegTypeId == 7) continue;

            if (finalVegTypeId == null) {
                if (isStart) {
                    finalVegTypeId = vegType;
                    isStart = false;
                } else if (vegType != null && vegType == 7) {
                    finalVegTypeId = 7;
                }
            } else if (vegType == null) {
                finalVegTypeId = null;
            } else if ((vegType == 2 && finalVegTypeId == 3) || (vegType == 3 && finalVegTypeId == 2)) {
                finalVegTypeId = 4;
            } else {
                finalVegTypeId = Math.max(finalVegTypeId, vegType);
            }
        }

        // 최종 채식유형 확정
        if (finalVegTypeId != null && finalVegTypeId != 7 && hasNull)
            finalVegTypeId = null;

        if (proVegTypeId != null)
            return new IngredientResponse(null, null, consumables, nonConsumables, unidentifiables);
        return new IngredientResponse(finalVegTypeId, String.join(", ", ingredients), consumables, nonConsumables, unidentifiables);
    }

    // 괄호 포함 여부 검사 메서드
    private boolean isBracketContains(String ingredient) {
        return ingredient.contains("(") || ingredient.contains("[");
    }

    // 괄호 밖 원재료 추출 메서드
    private String getMainIngredient(String ingredient) {
        int firstBracket = Math.min(
                ingredient.contains("(") ? ingredient.indexOf("(") : ingredient.length(),
                ingredient.contains("[") ? ingredient.indexOf("[") : ingredient.length()
        );
        return ingredient.substring(0, firstBracket).trim();
    }

    // 중첩된 괄호 처리 메서드
    private List<String> extractBracketContent(String ingredient) {
        List<String> results = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        StringBuilder currentContent = new StringBuilder();

        for (int i = 0; i < ingredient.length(); i++) {
            char c = ingredient.charAt(i);

            if (c == '(' || c == '[') {
                if (!stack.isEmpty()) currentContent.append(c);
                stack.push(c);
            } else if (c == ')' || c == ']') {
                if (!stack.isEmpty()) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        String[] items = currentContent.toString().trim().split(","); // 쉼표로 나누기
                        // 각 아이템을 리스트에 추가
                        results.addAll(Arrays.asList(items));
                        currentContent.setLength(0); // 초기화
                    } else {
                        currentContent.append(c);
                    }
                }
            } else {
                if (!stack.isEmpty()) currentContent.append(c);
            }
        }
        return results;
    }
}