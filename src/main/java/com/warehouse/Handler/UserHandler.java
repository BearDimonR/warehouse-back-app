package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.UserDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.User;
import com.warehouse.utils.QueryParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserHandler implements HttpHandler {

    Logger userLogger = LogManager.getLogger(UserHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
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
                    userLogger.error("Undefined request method: " + exchange.getRequestMethod());
                    exchange.sendResponseHeaders(400, -1);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            userLogger.error("Problem with user streams\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            userLogger.error("Trying to access user with wrong id");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                userLogger.error("Not unique user name\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                userLogger.error("Problem with server response\n\t" + e.getMessage());
            }
        } catch (Exception e) {
            userLogger.error("Undefined exception\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getUser(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if (params.isEmpty())
            getAllUsers(exchange);
        else
            getUser(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllUsers(HttpExchange exchange) throws IOException, SQLException {
        List<User> user = UserDAO.getInstance().getAll();
        OutputStream os = exchange.getResponseBody();
        String userJson = JsonProceed.getGson().toJson(user);
        exchange.sendResponseHeaders(200, 0);
        //Encrypt get User
        os.write(userJson.getBytes());
        os.flush();
    }

    private void getUser(HttpExchange exchange, long id) throws IOException, InvalidParameterException, SQLException {
        Optional<User> user = UserDAO.getInstance().get(id);
        if (user.isEmpty())
            throw new InvalidParameterException();
        OutputStream os = exchange.getResponseBody();
        String userJson = JsonProceed.getGson().toJson(user.get());
        exchange.sendResponseHeaders(200, 0);
        //Encrypt User
        os.write(userJson.getBytes());
        os.flush();
    }

    private void updateUser(HttpExchange exchange) throws IOException, InvalidParameterException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        //TODO decode input array
        User user = JsonProceed.getGson().fromJson(new String(input), User.class);
        if (!UserDAO.getInstance().update(user, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }

    private void createUser(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        //TODO decode input array
        User user = JsonProceed.getGson().fromJson(new String(input), User.class);
        UserDAO.getInstance().save(user);
        exchange.sendResponseHeaders(200, -1);
    }

    private void deleteUser(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        //TODO decode input array
        if (!UserDAO.getInstance().delete(Long.valueOf(id.get())))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }
}
