package com.warehouse.Handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.UserDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.User;
import com.warehouse.Model.auth.AuthenticatedUserDTO;
import com.warehouse.Model.auth.Credentials;
import com.warehouse.auth.Authentifaication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Optional;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
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
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Credentials credentials = JsonProceed.getGson().fromJson(new String(input), Credentials.class);
            Optional<User> loggedInUser = UserDAO.getInstance().getByCredentials(credentials);
            if (!loggedInUser.isEmpty()) {
                Optional<AuthenticatedUserDTO> user = Authentifaication.generateJWTToken(loggedInUser.get());
                Authentifaication.authentificate(user.get().getToken()).get().stream().forEach(a -> System.out.println(a));
                if (!user.isEmpty()) {
                    byte[] response = new Gson().toJson(user.get()).getBytes();
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
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
