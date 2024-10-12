package com.shinhan.VRRS.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> getMapFromJsonNode(JsonNode jsonNode) {
        Map<String, Object> map = null;
        try {
            map = objectMapper.convertValue(jsonNode, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
        return map;
    }

    public static List<Map<String, Object>> getListMapFromJsonArray(ArrayNode jsonArray) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (jsonArray != null) {
            for (JsonNode jsonNode : jsonArray) {
                Map<String, Object> map = getMapFromJsonNode(jsonNode);
                list.add(map);
            }
        }
        return list;
    }
}