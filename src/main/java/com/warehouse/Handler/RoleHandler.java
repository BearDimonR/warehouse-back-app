package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.warehouse.DAO.RoleDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Role;
import com.warehouse.utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoleHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getRole(exchange);
                break;
            case "PUT":
                putRole(exchange);
                break;
            case "POST":
                postRole(exchange);
                break;
            case "DELETE":
                deleteRole(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void getRole(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if(params.isEmpty())
            getAllRoles(exchange);
        else
            getRole(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllRoles(HttpExchange exchange) throws IOException {
        try {
            List<Role> roles = RoleDAO.getInstance().getAll();
            OutputStream os = exchange.getResponseBody();
            String roleJson = JsonProceed.getGson().toJson(roles);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt roles
            os.write(roleJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting roles streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting roles");
        }
    }

    private void getRole(HttpExchange exchange, long id) throws IOException {
        try {
            Optional<Role> role = RoleDAO.getInstance().get(id);
            if (role.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            String roleJson = JsonProceed.getGson().toJson(role.get());
            exchange.sendResponseHeaders(200, 0);
            //Encrypt role
            os.write(roleJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting role streams!");
            throw e;
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created role");
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting role");
        }
    }

    private void postRole(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Role role = JsonProceed.getGson().fromJson(new String(input), Role.class);
            RoleDAO.getInstance().save(role);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such role name already used!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating role");
            }
            exchange.close();
        }
    }

    private void putRole(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Role role = JsonProceed.getGson().fromJson(new String(input), Role.class);
            if (!RoleDAO.getInstance().update(role, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created role");
        } catch (SQLException e) {
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such role name already used!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when editing role");
            }
            exchange.close();
        }
    }

    private void deleteRole(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!RoleDAO.getInstance().delete(Long.parseLong(params.get("id"))))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting role");
        } catch (NullPointerException e) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            System.err.println("Null pointer in getting id");
        }
    }
}
