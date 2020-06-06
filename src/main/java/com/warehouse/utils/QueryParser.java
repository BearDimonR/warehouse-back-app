package com.warehouse.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {
    public static Map<String, String> parse(String query) {
        Map<String, String> params = new HashMap<>();

        if (query.matches("(\\w+=\\w)(&\\w+=\\w)*")) {
            Arrays.stream(query.split("&")).forEach(a -> params.put(a.split("=")[0], a.split("=")[1]));
        } else {
            //TODO add exception throwing
            System.out.println("Bad params");
        }

        return params;
    }
}
