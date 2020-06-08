package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.PermissionDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Permission;
import com.warehouse.Utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PermissionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getPermission(exchange);
                break;
            case "PUT":
                updatePermission(exchange);
                break;
            case "POST":
                createPermission(exchange);
                break;
            case "DELETE":
                deletePermission(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void getPermission(HttpExchange exchange) throws IOException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        try {
            String json = "";
            if (id.isEmpty()) {
                List<Permission> permission = PermissionDAO.getInstance().getAll();
                json = JsonProceed.getGson().toJson(permission);
            } else {
                Optional<Permission> permission = PermissionDAO.getInstance().get(Long.valueOf(id.get()));
                json = JsonProceed.getGson().toJson(permission.get());
            }
            exchange.sendResponseHeaders(200, 0);
            //TODO Encrypt permission
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting permission streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting permission");
        }
    }

    private void updatePermission(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Permission permission = JsonProceed.getGson().fromJson(new String(input), Permission.class);
            if (!PermissionDAO.getInstance().update(permission, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created permission");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such permission name already used!");
            } else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when editing permission");
            }
            exchange.close();
        }
    }

    private void createPermission(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Permission permission = JsonProceed.getGson().fromJson(new String(input), Permission.class);
            PermissionDAO.getInstance().save(permission);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such permission name already used!");
            } else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating permission");
            }
            exchange.close();
        }
    }

    private void deletePermission(HttpExchange exchange) throws IOException {
        try {
            Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
            //TODO decode input array
            if (!PermissionDAO.getInstance().delete(Long.valueOf(id.get())))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting permission");
        }
    }
}
