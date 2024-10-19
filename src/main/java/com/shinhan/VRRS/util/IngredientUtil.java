package com.shinhan.VRRS.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class IngredientUtil {
    private static final String[] KEYWORDS = {"제품명", "식품유형", "식품의", "제조", "기한", "내용량", "재질", "업소",
                                              "소재지", "성분", "참고", "제품은", "보관", "개봉", "판매", "방법",
                                              "(주)", "소비자", "상담", "직사광선"};
    private static final String[] ALLERGY_INGREDIENTS = {"밀", "대두", "쇠고기", "돼지고기", "닭고기", "우유", "메밀",
                                                        "토마토", "계란", "아황산류", "조개류", "알류", "난류"};
    private static final String[] COUNTRIES = {"미국", "캐나다", "호주", "중국", "러시아", "베트남", "프랑스", "브라질",
                                               "태국", "독일", "뉴질랜드", "이탈리아", "스페인", "일본", "멕시코",
                                               "네덜란드", "인도(?:네시아)?"};

    private static final double ERROR_MARGIN = 3.5;  // 오차 범위
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[\\[\\]:]");  // 특수문자 확인
    private static final Pattern INGREDIENT_SEPARATOR = Pattern.compile("^[*·]?[가-힣]+[:/]");  // 원재료 구분자 확인

    public static String extractReportNum(List<String> textList) {
        StringBuilder reportNum = new StringBuilder();  // 품목보고번호

        boolean isExtractingReportNumber = false;

        for (String text : textList) {
            // 1. 일반 키워드 확인
            if (isKeyword(text) || (text.contains("재료") || text.contains("원료"))) {
                if (isExtractingReportNumber) break;
                continue;
            }

            // 2. "품목" 키워드 확인
            if (text.contains("품목")) {
                if (text.contains(":")) {
                    text = text.split(":", 2)[1];
                    reportNum = new StringBuilder(text);
                }
                isExtractingReportNumber = true;
                continue;
            }

            // 3. 품목보고번호 추출 중이면 텍스트 추가
            if (isExtractingReportNumber)
                reportNum.append(text);
        }

        if (reportNum.isEmpty()) return null;
        String[] parts = reportNum.toString().split(",");
        return parts[0].replaceAll("[^0-9]", "");
    }

    public static List<String> extractIngredient(List<String> textList, List<Double> startCoords, List<Double> endCoords) {
        double prevEndX = 0.0;  // 이전 종료 좌표
        double ingredientsX = 0.0;  // 원재료 추출 좌표

        List<String> ingredients = new ArrayList<>();  // 원재료 리스트
        List<String> tempIngredients = new ArrayList<>();  // 임시 원재료 리스트
        List<String> deleteIngredients = new ArrayList<>();  // 삭제 원재료 리스트
        StringBuilder tempIngredient = new StringBuilder();  // 원재료를 저장할 변수
        StringBuilder allergy = new StringBuilder();  // 알레르기를 저장할 변수

        boolean isExtractingIngredients = false;  // 원재료 추출 여부
        boolean isSimpleShape = false;  // 심플 형태 여부
        boolean isStartKeyword = true;  // 시작 여부
        boolean isSkip = false;  // 줄 스킵 여부
        boolean isPrevDot = false;  // 단일 특수문자 여부
        boolean isEndComma = true;  // 콤마 종료 여부

        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            double startX = startCoords.get(i);
            double endX = endCoords.get(i);

            // 1. 새로운 줄 여부 확인 (이전 종료 좌표보다 시작 좌표가 작을 경우)
            if (startX < prevEndX) {
                if (!tempIngredient.isEmpty()) {
                    // 원재료 리스트에 추가
                    String temp = tempIngredient.toString();
                    if (isExtractingIngredients) {
                        // 참고사항으로 추정되면 종료
                        if ((temp.startsWith("*") || temp.startsWith("·")) && !(temp.contains(":") || temp.contains("/"))) {
                            tempIngredient.setLength(0);  // 원재료 초기화
                            break;
                        }
                        tempIngredients.add(tempIngredient.toString());
                    }
                    else {
                        // 콤마가 있거나 공백이 없고 숫자로만 구성되지 않으면 임시 원재료 리스트에 추가
                        if ((temp.contains(",") || !temp.contains(" ")) && !temp.matches("\\d+"))
                            tempIngredients.add(tempIngredient.toString().replaceAll("\\s+", ""));
                        else tempIngredients.clear();
                    }
                    tempIngredient.setLength(0);  // 원재료 초기화
                }
                isSkip = false;
            }

            prevEndX = endX;  // 종료 좌표 갱신

            // 텍스트가 "·" 또는 "*"이면 다음 텍스트와 연결
            if (text.equals("·") || text.equals("*")) {
                isPrevDot = true;
                continue;
            } else if (isPrevDot) {
                text = "*" + text;
                isPrevDot = false;
            }

            // 2. 일반 키워드 확인
            if (!isSkip) {
                if (isKeyword(text) || text.contains("품목")) {
                    // 원재료 추출 중이고 원재료 리스트가 비어있지 않으면 종료
                    if (isExtractingIngredients) {
                        if (!tempIngredients.isEmpty()) {
                            if (!isSimpleShape)
                                tempIngredient.setLength(0);
                            break;
                        }
                        isExtractingIngredients = false;
                    }
                    // 심플 형태로 판단
                    else if (isStartKeyword && !isSimpleShape && containsSpecialCharacter(text)) {
                        isSimpleShape = true;
                        continue;
                    }
                    // 다른 키워드가 나왔을 때 임시 원재료 리스트 삭제
                    deleteIngredients = tempIngredients;
                    tempIngredients.clear();
                    isStartKeyword = false;
                    isSkip = true;
                    continue;
                }
            }

            // 3. "원재료" 또는 "원료" 키워드 확인 (추출 시작)
            if (!isExtractingIngredients && (text.contains("재료") || text.contains("원료"))) {
                ingredientsX = startX;

                // 특수문자가 포함되면 심플 형태로 판단
                if (containsSpecialCharacter(text)) {
                    // "원재료" 키워드와 합쳐진 원재료 처리
                    if (text.contains(":")) {
                        String[] temp = text.split(":", 2);
                        if (temp.length > 1)
                            tempIngredient.append(temp[1].replaceAll("\\s+", ""));
                    }
                    isSimpleShape = true;
                }
                isExtractingIngredients = true;
                isStartKeyword = false;
                isSkip = false;
                continue;  // 키워드 자체는 저장하지 않고 넘어감
            }

            if (isSkip) continue;  // 줄 스킵

            // 4. 원재료로 추정되는 텍스트 처리
            if (!isSimpleShape && !isExtractingIngredients)
                if (containsKoreanCharacter(text)) tempIngredient.append(text);

            // 5. 원재료 추출 중이면 텍스트 추가
            if ((isSimpleShape || ingredientsX - ERROR_MARGIN < startX) && isExtractingIngredients) {
                if (ingredientsX > startX)
                    ingredientsX = startX;  // 원재료 추출 좌표 갱신

                text = text.replaceAll("\\s+", "");

                // 불필요한 텍스트 제외
                if (text.contains("및") || text.contains("함량")) {
                    // 특수문자가 포함되면 심플 형태로 판단
                    if (text.contains(":")) {
                        String[] temp = text.split(":", 2);
                        if (temp.length > 1)
                            tempIngredient.append(temp[1]);
                        isSimpleShape = true;
                    }
                    continue;
                }

                // 6. 주의사항으로 추정되면 종료
                if (text.startsWith("※")) break;

                // 7. 콤마로 끝나지 않았고 알레르기 원재료를 발견하면 종료
                if (!allergy.isEmpty()) {
                    if (isAllergyIngredient(text) || text.equals("함유")) break;
                    // 짤린 알레르기 원재료 처리
                    if (!text.contains(",") && textList.size() < i + 2) {
                        text += textList.get(++i);
                        if (isAllergyIngredient(text)) break;
                        // 알레르기 원재료가 아닌 경우
                        tempIngredient.append(allergy).append(text);
                        allergy.setLength(0);
                    }
                } else if ((!isEndComma && isAllergyIngredient(text))) {
                    if (text.contains(",")) {
                        String[] temp = text.split(",");
                        if (temp.length > 1) {
                            if (isAllergyIngredient(temp[1])) break;
                            // 짤린 알레르기 원재료 처리
                            if (!temp[1].contains(",") && textList.size() < i + 2) {
                                temp[1] += textList.get(++i);
                                if (isAllergyIngredient(temp[1])) break;
                            }
                            tempIngredient.append(text);
                        }
                    }
                    allergy.append(text);
                } else {
                    if (text.equals("함유")) continue;
                    if (!allergy.isEmpty()) {
                        tempIngredient.append(allergy).append(text);
                        allergy.setLength(0);
                    } else {
                        tempIngredient.append(text);
                    }
                }
                isEndComma = text.endsWith(",");  // 콤마 종료 여부
            }
        }

        // 마지막 원재료 처리
        if (!tempIngredient.isEmpty())
            tempIngredients.add(tempIngredient.toString());

        // 원재료명 키워드가 없는 경우 처리
        if (tempIngredients.isEmpty())
            tempIngredients = deleteIngredients;

        // 한 줄로 변경
        for (String ingredient : tempIngredients) {
            if (ingredients.isEmpty() || isIngredientSeparator(ingredient)) {
                ingredients.add(ingredient);
            } else {
                int line = ingredients.size() - 1;
                ingredients.set(line, ingredients.get(line) + ingredient);
            }
        }
        return ingredients;
    }

    // 원재료명만 추출하는 메서드
    public static List<String> extractCleanIngredient(List<String> textList) {
        List<String> cleanedIngredients = new ArrayList<>();

        for (String text : textList) {
            if (isIngredientSeparator(text))
                text = text.split("[:/]", 2)[1];

            text = removePercentagesAndBrackets(text);
            text = text.replaceAll("([)\\]])(?!,|\\s*[()\\[\\]])", "$1,");  // 닫는 괄호 뒤에 콤마 추가

            // 괄호 밖 ","를 기준으로 분리
            List<String> tempList = splitByCommaOutsideBrackets(text);

            for (String temp : tempList) {
                if (temp.isEmpty()) continue;
                else if (temp.contains("(") || temp.contains("[")) {
                    temp = removeOrigin(temp);
                    if(temp.contains("[")) {
                        temp = modifyIngredients(temp);
                        // 괄호 밖 ","를 기준으로 분리
                        List<String> tList = splitByCommaOutsideBrackets(temp);
                        if (tList.size() > 1) {
                            for (String t : tList) {
                                t = t.replaceAll("([가-힣])\\d+종|([가-힣])\\d+(?![가-힣])", "$1$2"); // 한글 뒤 숫자 제거
                                cleanedIngredients.add(t);
                            }
                            continue;
                        }
                    }
                    if (!temp.contains("(") && temp.contains("["))
                        temp = temp.replace("[", "(").replace("]", ")");
                }
                temp = temp.replaceAll("([가-힣])\\d+종|([가-힣])\\d+(?![가-힣])", "$1$2"); // 한글 뒤 숫자 제거
                cleanedIngredients.add(temp);
            }
        }

        return removeDuplicates(cleanedIngredients); // 중복 제거
    }

    // 괄호 처리 메서드
    public static List<String> processIngredients(List<String> ingredients) {
        for (int i = 0; i < ingredients.size(); i++) {
            String text = ingredients.get(i);

            if (text.matches(".*[\\[\\]{}].*")) {
                text = text.replace("{", "[").replace("}", "]");
                text = fixBrackets(text);
            }
            if (!areBracketsBalanced(text)) return null;

            ingredients.set(i, text);
        }
        return ingredients; // 처리된 원재료 리스트 반환
    }

    // 문자열 분리 메서드
    public static List<String> splitByCommaOutsideBrackets(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        int openParenCount = 0; // 소괄호의 개수를 추적
        int openSquareBracketCount = 0; // 대괄호의 개수를 추적

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // 괄호의 열림과 닫힘을 추적
            if (ch == '(')
                openParenCount++;
            else if (ch == ')')
                openParenCount--;
            else if (ch == '[')
                openSquareBracketCount++;
            else if (ch == ']')
                openSquareBracketCount--;

            // 콤마를 만났을 때 괄호가 모두 닫혀있으면 분리
            if (ch == ',' && openParenCount == 0 && openSquareBracketCount == 0) {
                result.add(currentPart.toString().trim());
                currentPart.setLength(0); // StringBuilder 초기화
            } else {
                currentPart.append(ch);
            }
        }

        // 마지막 부분 추가
        if (!currentPart.isEmpty())
            result.add(currentPart.toString().trim());
        return result;
    }

    // 일반 키워드 확인 메서드 (제품명, 식품유형 등)
    private static boolean isKeyword(String input) {
        for (String keyword : KEYWORDS)
            if (input.contains(keyword)) return true;
        return false;
    }

    // 특수 문자 확인 메서드
    private static boolean containsSpecialCharacter(String input) {
        if (input.startsWith("*") || input.startsWith("·")) return true;
        return SPECIAL_CHAR_PATTERN.matcher(input).find();
    }

    // 한글 확인 메서드
    private static boolean containsKoreanCharacter(String input) {
        if (input.matches(".*[(),{}\\[\\]，].*")) return true;
        return input.matches(".*[가-힣]+.*");
    }

    // 알레르기 원재료 확인 메서드
    private static boolean isAllergyIngredient(String input) {
        for (String allergy : ALLERGY_INGREDIENTS) {
            if (input.contains("*"))
                input = input.replace("*", "");
            if (input.startsWith(allergy)) return true;
        }
        return false;
    }

    // 원재료 구분자 확인 메소드
    private static boolean isIngredientSeparator(String input) {
        return INGREDIENT_SEPARATOR.matcher(input).find();
    }

    // 괄호 짝이 맞는지 확인하는 메서드
    private static boolean areBracketsBalanced(String input) {
        Stack<Character> stack = new Stack<>();
        for (char ch : input.toCharArray()) {
            if (ch == '(' || ch == '[') {
                stack.push(ch);
            } else if (ch == ')' || ch == ']') {
                if (stack.isEmpty()) return false;
                char openBracket = stack.pop();
                if (!isMatchingPair(openBracket, ch)) return false;
            }
        }
        return stack.isEmpty();
    }

    // 열린 괄호와 닫힌 괄호가 짝이 맞는지 확인하는 메서드
    private static boolean isMatchingPair(char openBracket, char closeBracket) {
        return (openBracket == '(' && closeBracket == ')') ||
               (openBracket == '[' && closeBracket == ']');
    }

    // 괄호 수정 메서드
    private static String fixBrackets(String input) {
        char[] result = new char[input.length()];
        int lastOpenIndex = -1;
        boolean lastWasOpen = false;  // 이전 문자가 여는 소괄호였는지 추적
        boolean lastWasClose = false; // 이전 문자가 닫는 소괄호였는지 추적

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '(') {
                // 이전에 열린 소괄호가 나왔으면 이번 첫 번째 소괄호를 대괄호로 바꿈
                if (lastWasOpen) {
                    result[lastOpenIndex] = '[';
                    lastWasOpen = false;
                } else {
                    lastOpenIndex = i;
                    lastWasOpen = true;
                }
                result[i] = ch;
                lastWasClose = false;
            } else if (ch == ')') {
                // 이전에 닫힌 소괄호가 나왔으면 이번 닫힌 소괄호를 대괄호로 바꿈
                if (lastWasClose) result[i] = ']';
                else result[i] = ch;
                lastWasOpen = false;
                lastWasClose = true;
            } else {
                result[i] = ch;
            }
        }
        return new String(result);
    }

    // 대괄호 처리 메서드
    private static String modifyIngredients(String ingredients) {
        // 대괄호 개수 및 소괄호 포함 여부 확인
        int openBrackets = 0;
        boolean hasParentheses = ingredients.contains("(");

        // 중첩된 대괄호 체크
        for (char ch : ingredients.toCharArray())
            if (ch == '[')
                openBrackets++;

        // 대괄호가 중첩된 경우
        if (openBrackets > 1) {
            // 소괄호가 있으면 외부 대괄호 제거, 없으면 내부 대괄호를 소괄호로 변환
            if (hasParentheses)
                ingredients = ingredients.replaceFirst("^[^\\[]*\\[", "").replaceFirst("]$", "");
            else
                ingredients = ingredients.replaceAll("\\[([^\\[\\]]+)]", "($1)");
        }

        return ingredients; // 어떤 조건에도 해당하지 않는 경우 원본 반환
    }

    // 불필요한 문자 제거 메서드
    private static String removePercentagesAndBrackets(String input) {
        return input.replaceAll("\\(\\d+\\)", "") // 숫자만 있는 괄호 제거
                .replaceAll("\\d+\\.\\d*%|\\d+%", "") // 함량 제거
                .replaceAll("[^\\p{IsHangul}\\w\\s()\\[\\],:/-]", ""); // 불필요한 특수문자 제거
    }

    // 원산지 제거 함수
    private static String removeOrigin(String input) {
        // 배열을 정규식으로 변환하여 국가명 제거
        String countryPattern = String.join("|", COUNTRIES);

        return input.replaceAll("\\(외국산:[^)]*\\)", "")  // '외국산:'이 들어간 소괄호 전체 제거
                .replaceAll("외국산\\([^)]*\\)", "")  // '외국산' 뒤에 나오는 소괄호 제거
                .replaceAll("\\b[가-힣]+산\\b", "")  // 'OO산' 형태의 원산지 제거
                .replaceAll("\\b(" + countryPattern + ")\\b", "")  // 국가명 제거
                .replaceAll(",\\)", ")")  // 콤마 제거 후 괄호 닫기
                .replaceAll("\\([^가-힣]*\\)", "")  // 소괄호 안에 한글이 없는 경우 괄호 제거
                .replaceAll("\\[[^가-힣]*]", "")  // 대괄호 안에 한글이 없는 경우 제거
                .replaceAll("[:/]", "");  // 남은 특수문자 제거
    }

    // 중복 제거 함수 (순서 유지)
    private static List<String> removeDuplicates(List<String> input) {
        return new ArrayList<>(new LinkedHashSet<>(input));
    }
}