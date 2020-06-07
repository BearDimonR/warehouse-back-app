package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.RolePermissionDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.RolePermissionConnection;
import com.warehouse.Model.RolePermissions;
import com.warehouse.utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

public class RolePermissionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getRolePermissions(exchange);
                break;
            case "PUT":
                //not implemented response
                exchange.sendResponseHeaders(501,0);
                exchange.close();
                break;
            case "POST":
                postRolePermission(exchange);
                break;
            case "DELETE":
                deleteRolePermission(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void getRolePermissions(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        try {
            RolePermissions rolePermission =
                    RolePermissionDAO.getInstance().get(Long.parseLong(params.get("id")));
            OutputStream os = exchange.getResponseBody();
            String permJson = JsonProceed.getGson().toJson(rolePermission);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt rolePermission
            os.write(permJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting rolePermission streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting rolePermission");
        }
    }

    private void postRolePermission(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            RolePermissionConnection rolePermissionConnection =
                    JsonProceed.getGson().fromJson(new String(input), RolePermissionConnection.class);
            RolePermissionDAO.getInstance().save(rolePermissionConnection);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such connection already present!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating roleId, permissionId connection");
            }
            exchange.close();
        }
    }

    private void deleteRolePermission(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!RolePermissionDAO.getInstance().delete(
                    new RolePermissionConnection(
                            Long.parseLong(params.get("roleId")),
                            Long.parseLong(params.get("permissionId")))))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting rolePermission");
        } catch (NullPointerException e) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            System.err.println("Null pointer in getting rolePermission ids");
        }
    }
}
