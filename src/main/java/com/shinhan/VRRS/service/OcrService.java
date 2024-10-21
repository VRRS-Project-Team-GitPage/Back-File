package com.shinhan.VRRS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shinhan.VRRS.dto.OcrResponse;
import com.shinhan.VRRS.util.IngredientUtil;
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
        // Gson 객체 생성
        Gson gson = new Gson();

        // JSON 파싱
        JsonObject jobj = JsonParser.parseString(response).getAsJsonObject();

        // images 배열 가져오기
        JsonArray jArray = jobj.getAsJsonArray("images");
        JsonObject jsonObjImage = jArray.get(0).getAsJsonObject();
        JsonArray fields = jsonObjImage.getAsJsonArray("fields");

        List<String> textList = new ArrayList<>();
        List<Double> startCoords = new ArrayList<>();
        List<Double> endCoords = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            JsonObject field = fields.get(i).getAsJsonObject();

            // inferText 추출
            String inferText = field.get("inferText").getAsString();
            textList.add(inferText);

            // "boundingPoly"에서 "vertices"의 값을 추출
            JsonObject boundingPoly = field.getAsJsonObject("boundingPoly");
            JsonArray vertices = boundingPoly.getAsJsonArray("vertices");

            if (vertices != null && vertices.size() >= 2) {
                // 첫 번째 vertex의 x 값을 저장
                double x1 = vertices.get(0).getAsJsonObject().get("x").getAsDouble();
                startCoords.add(x1);

                // 두 번째 vertex의 x 값을 저장
                double x2 = vertices.get(1).getAsJsonObject().get("x").getAsDouble();
                endCoords.add(x2);
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