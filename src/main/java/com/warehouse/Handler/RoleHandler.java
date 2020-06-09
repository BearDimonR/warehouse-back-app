package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.warehouse.DAO.RoleDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Role;
import com.warehouse.Utils.QueryParser;
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

public class RoleHandler implements HttpHandler {

    Logger roleLogger = LogManager.getLogger(RoleHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
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
                    roleLogger.error("Undefined request method: " + exchange.getRequestMethod());
                    exchange.sendResponseHeaders(400, -1);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            roleLogger.error("Problem with role streams\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            roleLogger.error("Trying to access with wrong id");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                roleLogger.error("Not unique name or id which already used\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                roleLogger.error("Problem with server response\n\t" + e.getMessage());
            }
        } catch (Exception e) {
            roleLogger.error("Undefined exception\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getRole(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if (params.isEmpty())
            getAllRoles(exchange);
        else
            getRole(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllRoles(HttpExchange exchange) throws IOException, SQLException {
        List<Role> roles = RoleDAO.getInstance().getAll();
        OutputStream os = exchange.getResponseBody();
        String roleJson = JsonProceed.getGson().toJson(roles);
        exchange.sendResponseHeaders(200, 0);
        //Encrypt roles
        os.write(roleJson.getBytes());
        os.flush();
    }

    private void getRole(HttpExchange exchange, long id) throws IOException, SQLException, InvalidParameterException {
        Optional<Role> role = RoleDAO.getInstance().get(id);
        if (role.isEmpty())
            throw new InvalidParameterException();
        OutputStream os = exchange.getResponseBody();
        String roleJson = JsonProceed.getGson().toJson(role.get());
        exchange.sendResponseHeaders(200, -1);
        //Encrypt role
        os.write(roleJson.getBytes());
        os.flush();
    }

    private void postRole(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        // decode input array
        Role role = JsonProceed.getGson().fromJson(new String(input), Role.class);
        RoleDAO.getInstance().save(role);
        exchange.sendResponseHeaders(200, -1);
    }

    private void putRole(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        // decode input array
        Role role = JsonProceed.getGson().fromJson(new String(input), Role.class);
        if (!RoleDAO.getInstance().update(role, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }

    private void deleteRole(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if (!RoleDAO.getInstance().delete(Long.parseLong(params.get("id"))))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }
}
