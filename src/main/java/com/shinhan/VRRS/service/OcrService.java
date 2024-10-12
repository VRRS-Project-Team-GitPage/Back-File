package com.shinhan.VRRS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.shinhan.VRRS.dto.OcrResponse;
import com.shinhan.VRRS.util.IngredientUtil;
import com.shinhan.VRRS.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class OcrService {
    @Value("${naver.clova.api.url}")
    private String apiUrl;

    @Value("${naver.clova.api.secret}")
    private String secretKey;

    public StringBuffer callOcr(MultipartFile file) throws IOException {
        String format = Objects.requireNonNull(file.getContentType()).split("/")[1]; // 포멧
        byte[] imageBytes = file.getBytes(); // 파일 -> 바이트 배열

        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setUseCaches(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setRequestProperty("X-OCR-SECRET", secretKey);

        JSONObject json = new JSONObject();
        json.put("version", "V2");
        json.put("requestId", UUID.randomUUID().toString());
        json.put("timestamp", System.currentTimeMillis());
        JSONObject image = new JSONObject();
        image.put("format", format);
        image.put("data", imageBytes);
        image.put("name", "demo");
        JSONArray images = new JSONArray();
        images.put(image);
        json.put("images", images);
        String postParams = json.toString();

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        BufferedReader br;
        if (responseCode == 200)
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) response.append(inputLine);
        br.close();

        return response;
    }

//    public List<String> parseJson(StringBuffer response) {
    public OcrResponse parseJson(String response) throws JsonProcessingException {
        //json 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jobj = objectMapper.readTree(response);

        //images 배열 -> obj 화
        JsonNode jArray = jobj.get("images");
        JsonNode JSONObjImage = jArray.get(0);
        ArrayNode fields = (ArrayNode) JSONObjImage.get("fields");

        List<Map<String, Object>> m = JsonUtil.getListMapFromJsonArray(fields);
        List<String> textList = new ArrayList<>();
        List<Double> startCoords = new ArrayList<>(); // vertices 리스트 추가
        List<Double> endCoords = new ArrayList<>(); // vertices 리스트 추가

        for (Map<String, Object> as : m) {
            textList.add((String) as.get("inferText"));

            // "boundingPoly"에서 "vertices"의 값을 추출
            Map<String, Object> boundingPoly = (Map<String, Object>) as.get("boundingPoly");
            List<Map<String, Object>> vertices = (List<Map<String, Object>>) boundingPoly.get("vertices");

            if (vertices != null && !vertices.isEmpty()) {
                Map<String, Object> vertex1 = vertices.get(0);
                double x = (double) vertex1.get("x");

                // 첫 번째 vertex의 x 값을 저장
                startCoords.add(x);

                Map<String, Object> vertex2 = vertices.get(1);
                x = (double) vertex2.get("x");

                // 두 번째 vertex의 x 값을 저장
                endCoords.add(x);
            }
        }

        String reportNum = IngredientUtil.extractReportNum(textList);
        List<String> ingredients = IngredientUtil.extractIngredient(textList, startCoords, endCoords);
        List<String> processedIngredients = IngredientUtil.processIngredients(ingredients);

        // 괄호 짝이 맞지 않으면 원본 원재료 리스트를 반환
        if (processedIngredients == null)
            return new OcrResponse(reportNum, ingredients, false);

        List<String> cleanIngredients = IngredientUtil.extractCleanIngredient(processedIngredients);
        return new OcrResponse(reportNum, cleanIngredients, true);
    }
}