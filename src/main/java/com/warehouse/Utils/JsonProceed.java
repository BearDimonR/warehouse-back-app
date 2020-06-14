package com.warehouse.Utils;

import com.google.gson.Gson;

public class JsonProceed {

    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}
