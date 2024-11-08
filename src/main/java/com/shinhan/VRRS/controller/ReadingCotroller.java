package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.*;
import com.shinhan.VRRS.dto.request.IngredientRequest;
import com.shinhan.VRRS.dto.response.IngredientResponse;
import com.shinhan.VRRS.dto.response.OcrResponse;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.service.ProductService;
import com.shinhan.VRRS.service.RecommendationService;
import com.shinhan.VRRS.service.ReadingService;
import com.shinhan.VRRS.service.OcrService;
import com.shinhan.VRRS.util.IngredientUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/reading")
@RequiredArgsConstructor
public class ReadingCotroller {
    private final OcrService ocrService;
    private final ProductService productService;
    private final ReadingService readingService;
    private final RecommendationService recommendationService;

    // 텍스트 호출
    @PostMapping("/ocr")
    public ResponseEntity<OcrResponse> callOcr(@RequestParam("file") MultipartFile file) {
        OcrResponse processedText;
        try {
            StringBuffer ocrResponse = ocrService.callOcr(file); // 클로바 OCR API 호출
            processedText = ocrService.parseJson(ocrResponse); // 응답 텍스트 전처리

            // 등록 제품 판단
            String reportNum = processedText.getReportNum();
            if (reportNum != null) {
                Product product = productService.getProductNotThrows(reportNum);
                if (product != null)
                    processedText.setExists(true);
            }

            // 괄호 검사
            if (!processedText.isFullBracket())
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(processedText); // 422 Unprocessable Entity
            return ResponseEntity.ok(processedText);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Sever Error
        }
    }

    // 제품 판독 및 추천
    @PostMapping
    public ResponseEntity<IngredientResponse> checkIngredients(@Valid @RequestBody IngredientRequest request) {
        Long proId = null;
        Integer proVegTypeId = null;
        Integer userVegTypeId = request.getVegTypeId();

        List<String> ingredients;
        String reportNum = request.getReportNum();

        // 등록 제품 처리
        if (request.isExists()) {
            Product product = productService.getProductNotThrows(reportNum);
            proVegTypeId = product.getVegType().getId();
            proId = product.getId();
            ingredients = IngredientUtil.splitByCommaOutsideBrackets(product.getIngredients());
        } else {
            // 공백 및 콤마 제거
            String cleanIngredients = request.getIngredients()
                    .replaceAll("\\s+", "")
                    .replaceAll("\\(,", "(")
                    .replaceAll(",\\)", ")")
                    .replaceAll(",{2,}", ",");

            List<String> ingredienSingleList = Collections.singletonList(cleanIngredients);
            ingredienSingleList = IngredientUtil.processIngredients(ingredienSingleList);

            // 괄호 검사
            if (ingredienSingleList == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

            // 불필요한 문자 제거
            if (!request.isFullBracket())
                ingredienSingleList = IngredientUtil.extractCleanIngredient(ingredienSingleList);

            ingredients = IngredientUtil.splitByCommaOutsideBrackets(String.join(", ", ingredienSingleList));
        }

        // 제품 판독
        IngredientResponse result = readingService.checkConsumable(ingredients, userVegTypeId, proVegTypeId);

        // 유사품 추천
        String consumables = String.join(", ", result.getConsumables());
        List<ProductDTO> products = recommendationService.recommendByReading(userVegTypeId, consumables);

        result.setProId(proId);
        result.setReportNum(reportNum);
        result.setRecommendations(products);
        return ResponseEntity.ok(result);
    }
}