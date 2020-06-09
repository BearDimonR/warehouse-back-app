package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.RolePermissionDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.RolePermissionConnection;
import com.warehouse.Model.RolePermissions;
import com.warehouse.Utils.QueryParser;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Map;

public class RolePermissionHandler extends AbstractHandler {

    public RolePermissionHandler() {
        getPermission = "";
        updatePermission = "";
        createPermission = "";
        deletePermission = "";

        logger = LogManager.getLogger(RolePermissionHandler.class);
        model = RolePermissionConnection.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException {
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

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            RolePermissionConnection rolePermissionConnection =
                    JsonProceed.getGson().fromJson(new String(input), RolePermissionConnection.class);
            if(!RolePermissionDAO.getInstance().save(rolePermissionConnection))
                throw new InvalidParameterException();
            exchange.sendResponseHeaders(200, -1);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException {
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
