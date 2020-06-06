package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.UserDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.User;
import com.warehouse.utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getUser(exchange);
                break;
            case "PUT":
                updateUser(exchange);
                break;
            case "POST":
                createUser(exchange);
                break;
            case "DELETE":
                deleteUser(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void getUser(HttpExchange exchange) throws IOException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        try {
            String json = "";
            if (id.isEmpty()) {
                List<User> user = UserDAO.getInstance().getAll();
                json = JsonProceed.getGson().toJson(user);
            } else {
                Optional<User> permission = UserDAO.getInstance().get(Long.valueOf(id.get()));
                json = JsonProceed.getGson().toJson(permission.get());
            }
            exchange.sendResponseHeaders(200, 0);
            //TODO Encrypt User
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting User streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting User");
        }
    }

    private void updateUser(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            User user = JsonProceed.getGson().fromJson(new String(input), User.class);
            if (!UserDAO.getInstance().update(user, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created User");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such User name already used!");
            } else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when editing User");
            }
            exchange.close();
        }
    }

    private void createUser(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            User user = JsonProceed.getGson().fromJson(new String(input), User.class);
            UserDAO.getInstance().save(user);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such User name already used!");
            } else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating User");
            }
            exchange.close();
        }
    }

    private void deleteUser(HttpExchange exchange) throws IOException {
        try {
            Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
            //TODO decode input array
            if (!UserDAO.getInstance().delete(Long.valueOf(id.get())))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting User");
        }
    }
}
