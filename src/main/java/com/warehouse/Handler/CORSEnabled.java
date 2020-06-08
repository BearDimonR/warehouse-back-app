package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface CORSEnabled {
    default void enableCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            try {
                exchange.sendResponseHeaders(200, -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
