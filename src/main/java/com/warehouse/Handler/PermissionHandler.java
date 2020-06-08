package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.PermissionDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Permission;
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

public class PermissionHandler implements HttpHandler {

    Logger permissionLogger = LogManager.getLogger(PermissionHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

        switch (exchange.getRequestMethod()) {
            case "GET":
                getPermission(exchange);
                break;
            case "PUT":
                putPermission(exchange);
                break;
            case "POST":
                postPermission(exchange);
                break;
            case "DELETE":
                deletePermission(exchange);
                break;
            default:
                permissionLogger.error("Undefined request method: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(400, -1);        }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            permissionLogger.error("Problem with permission streams\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            permissionLogger.error("Trying to access permission with wrong id");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                permissionLogger.error("Not unique permission name\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                permissionLogger.error("Problem with server response\n\t" + e.getMessage());
            }
        } catch (Exception e) {
            permissionLogger.error("Undefined exception\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getPermission(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if(params.isEmpty())
            getAllPermissions(exchange);
        else
            getPermission(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllPermissions(HttpExchange exchange) throws IOException, SQLException {
            List<Permission> permission = PermissionDAO.getInstance().getAll();
            OutputStream os = exchange.getResponseBody();
            String permissionJson = JsonProceed.getGson().toJson(permission);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt permission
            os.write(permissionJson.getBytes());
            os.flush();
    }

    private void getPermission(HttpExchange exchange, long id) throws IOException, SQLException, InvalidParameterException {
            Optional<Permission> permission = PermissionDAO.getInstance().get(id);
            if (permission.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            String permissionJson = JsonProceed.getGson().toJson(permission.get());
            exchange.sendResponseHeaders(200, 0);
            //Encrypt permission
            os.write(permissionJson.getBytes());
            os.flush();
    }

    private void putPermission(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Permission permission = JsonProceed.getGson().fromJson(new String(input), Permission.class);
            if (!PermissionDAO.getInstance().update(permission, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }

    private void postPermission(HttpExchange exchange) throws IOException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Permission permission = JsonProceed.getGson().fromJson(new String(input), Permission.class);
            PermissionDAO.getInstance().save(permission);
            exchange.sendResponseHeaders(200, -1);
    }

    private void deletePermission(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
            //TODO decode input array
            if (!PermissionDAO.getInstance().delete(Long.valueOf(id.get())))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }
}
