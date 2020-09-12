package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface CORSEnabled {
    default void enableCORS(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    }
}
