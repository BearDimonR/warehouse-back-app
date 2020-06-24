package com.warehouse.Utils;

import com.warehouse.Http.Server;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private static Logger root = Server.root;

    public static Map<String, String> parse(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) return new HashMap<>();
        if (query.matches("(\\w+=[\\S\\s]+)(&\\w+=[\\S\\s]+)*")) {
            Arrays.stream(query.split("&")).forEach(a -> params.put(a.split("=")[0], a.split("=")[1]));
        } else {
            root.warn("Bad params in query.");
        }

        return params;
    }
}
