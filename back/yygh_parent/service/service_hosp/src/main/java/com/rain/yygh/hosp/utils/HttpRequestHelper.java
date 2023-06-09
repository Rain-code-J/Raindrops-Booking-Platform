package com.rain.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequestHelper {
    public static Map<String, Object> switchMap(Map<String, String[]> parameterMap) {
        Map<String, Object> resultMap = new HashMap<>();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            resultMap.put(key,value);
        }

        return resultMap;
    }
}
