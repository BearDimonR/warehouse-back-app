package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.RolePermissionDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.RolePermissionConnection;
import com.warehouse.Model.RolePermissions;
import com.warehouse.utils.QueryParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Map;

public class RolePermissionHandler implements HttpHandler {

    Logger rolePermissionLogger = LogManager.getLogger(RolePermissionHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getRolePermissions(exchange);
                break;
            case "PUT":
                //not implemented response
                exchange.sendResponseHeaders(501,-1);
                exchange.close();
                break;
            case "POST":
                postRolePermission(exchange);
                break;
            case "DELETE":
                deleteRolePermission(exchange);
                break;
            default:
                rolePermissionLogger.error("Undefined request method: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(400, -1);
        }
    } catch (IOException e) {
        exchange.sendResponseHeaders(500, -1);
        rolePermissionLogger.error("Problem with rolePermission streams\n\t" + e.getMessage());
    } catch (InvalidParameterException e) {
        exchange.sendResponseHeaders(404, -1);
        rolePermissionLogger.error("Trying to access rolePermission with wrong id");
    } catch (SQLException e) {
        if (e.getSQLState().equals("23505")) {
            exchange.sendResponseHeaders(409, -1);
            rolePermissionLogger.error("Not unique rolePermission\n\t" + e.getMessage());
        } else {
            exchange.sendResponseHeaders(500, -1);
            rolePermissionLogger.error("Problem with server response\n\t" + e.getMessage());
        }
    } catch (Exception e) {
            rolePermissionLogger.error("Undefined exception\n\t" + e.getMessage());
    } finally {
        exchange.close();
    }
    }

    private void getRolePermissions(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            RolePermissions rolePermission =
                    RolePermissionDAO.getInstance().get(Long.parseLong(params.get("id")));
            OutputStream os = exchange.getResponseBody();
            String permJson = JsonProceed.getGson().toJson(rolePermission);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt rolePermission
            os.write(permJson.getBytes());
            os.flush();
    }

    private void postRolePermission(HttpExchange exchange) throws IOException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            RolePermissionConnection rolePermissionConnection =
                    JsonProceed.getGson().fromJson(new String(input), RolePermissionConnection.class);
            RolePermissionDAO.getInstance().save(rolePermissionConnection);
            exchange.sendResponseHeaders(200, -1);
    }

    private void deleteRolePermission(HttpExchange exchange) throws IOException, SQLException {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!RolePermissionDAO.getInstance().delete(
                    new RolePermissionConnection(
                            Long.parseLong(params.get("roleId")),
                            Long.parseLong(params.get("permissionId")))))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }
}
