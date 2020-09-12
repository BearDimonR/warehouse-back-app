package com.warehouse.Http;

import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
@Builder
public class Response {
    Object data;
    Integer statusCode;
    HttpExchange httpExchange;
}
