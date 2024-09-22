package com.shinhan.VRRS.util;

import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonObject(JsonObject jsonObject) {
        Map<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(jsonObject.toString(), Map.class);
        } catch (IOException e) {
            log.error("Error: {}", e.getMessage());
        }
        return map;
    }

    public static List<Map<String, Object>> getListMapFromJsonArray(JsonArray jsonArray) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (jsonArray != null) {
            int jsonSize = jsonArray.size();

            for (int i = 0; i < jsonSize; i++) {
                Map<String, Object> map = getMapFromJsonObject((JsonObject)jsonArray.get(i));
                list.add(map);
            }
        }
        return list;
    }
}