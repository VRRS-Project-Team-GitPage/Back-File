package com.shinhan.VRRS.service.readingService;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.shinhan.VRRS.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

    private final RestTemplate restTemplate = new RestTemplate();

    public StringBuffer ocr(MultipartFile file) throws IOException {
        String format = file.getContentType().split("/")[1];
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

    public List<String> parseJson(String response) {
        //json 파싱
        Gson gson = new Gson();
//        JsonObject jobj = gson.fromJson(response.toString(), JsonObject.class);
        JsonObject jObj = gson.fromJson(response, JsonObject.class); // 테스트용
        System.out.println("(빈 json: " + jObj.isEmpty() + ")");

        //images 배열 -> obj 화
        JsonArray jArray = (JsonArray) jObj.get("images");
        JsonObject JSONObjImage = (JsonObject) jArray.get(0);
        JsonArray s = (JsonArray) JSONObjImage.get("fields");

        List<Map<String, Object>> m = JsonUtil.getListMapFromJsonArray(s);
        List<String> result = new ArrayList<>();
        for (Map<String, Object> as : m) result.add((String) as.get("inferText"));

        return result;
    }
}