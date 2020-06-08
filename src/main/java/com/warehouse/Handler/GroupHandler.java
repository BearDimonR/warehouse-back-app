package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.GroupDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Group;
import com.warehouse.Utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GroupHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getGroup(exchange);
                break;
            case "PUT":
                putGroup(exchange);
                break;
            case "POST":
                postGroup(exchange);
                break;
            case "DELETE":
                deleteGroup(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void getGroup(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if(params.isEmpty())
            getAllGroups(exchange);
        else
            getGroup(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllGroups(HttpExchange exchange) throws IOException {
        try {
            List<Group> groups = GroupDAO.getInstance().getAll();
            OutputStream os = exchange.getResponseBody();
            String groupJson = JsonProceed.getGson().toJson(groups);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt groups
            os.write(groupJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting groups streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting groups");
        }
    }

    private void getGroup(HttpExchange exchange, long id) throws IOException {
        try {
            Optional<Group> group = GroupDAO.getInstance().get(id);
            if (group.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            String groupJson = JsonProceed.getGson().toJson(group.get());
            exchange.sendResponseHeaders(200, 0);
            //Encrypt group
            os.write(groupJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting group streams!");
            throw e;
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created group");
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting group");
        }
    }

    private void postGroup(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Group group = JsonProceed.getGson().fromJson(new String(input), Group.class);
            GroupDAO.getInstance().save(group);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            e.printStackTrace();
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such group name already used!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating group");
            }
            exchange.close();
        }
    }

    private void putGroup(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Group group = JsonProceed.getGson().fromJson(new String(input), Group.class);
            if (!GroupDAO.getInstance().update(group, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created group");
        } catch (SQLException e) {
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such group name already used!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when editing group");
            }
            exchange.close();
        }
    }

    private void deleteGroup(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!GroupDAO.getInstance().delete(Long.parseLong(params.get("id"))))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting group");
        } catch (NullPointerException e) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            System.err.println("Null pointer in getting id");
        }
    }
}
