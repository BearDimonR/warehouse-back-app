package com.warehouse.View;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Http.Response;
import com.warehouse.Utils.JsonProceed;

import java.io.IOException;
import java.io.OutputStream;

public class JsonView implements View {

    @Override
    public void view(Response response) {
        Object data = response.getData();
        HttpExchange exchange = response.getHttpExchange();

        byte[] body = JsonProceed.getGson().toJson(data).getBytes();
        Integer status = response.getStatusCode();

        try {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length);
            OutputStream os = exchange.getResponseBody();
            os.write(body);
            os.flush();
            os.close();
        } catch (IOException e) {

        }
    }
}
