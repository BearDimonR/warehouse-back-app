package com.warehouse.Handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.UserDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.User;
import com.warehouse.Model.auth.AuthenticatedUserDTO;
import com.warehouse.Model.auth.Credentials;
import com.warehouse.Authentication.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Optional;

public class LoginHandler implements HttpHandler, CORSEnabled {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        enableCORS(exchange);
        switch (exchange.getRequestMethod()) {
            case "POST":
                login(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void login(HttpExchange exchange) {
        try {
            byte[] input = exchange.getRequestBody().readAllBytes();
            //TODO decode input array
            Optional<User> userByCredentials = UserDAO.getInstance().getByCredentials(JsonProceed.getGson().fromJson(new String(input), Credentials.class));
            if (userByCredentials.isPresent()) {
                Optional<AuthenticatedUserDTO> user = Authentication.generateLoginResponse(userByCredentials.get());
                if (user.isPresent()) {
                    byte[] response = new Gson().toJson(user.get()).getBytes();
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                    exchange.close();
                }
            } else {
                exchange.sendResponseHeaders(403, -1);
                System.err.println("Bad credentials. Access denied.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
