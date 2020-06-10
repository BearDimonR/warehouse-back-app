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
        getPermission = "read_permission_role_connection";
        updatePermission = "";
        createPermission = "assign_permission_to_role";
        deletePermission = "unassign_permission_from_role";

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
    protected long create(HttpExchange exchange) throws IOException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            RolePermissionConnection rolePermissionConnection =
                    JsonProceed.getGson().fromJson(new String(input), RolePermissionConnection.class);
            long id = RolePermissionDAO.getInstance().save(rolePermissionConnection);
            exchange.sendResponseHeaders(200, 0);
            return id;
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
                exchange.sendResponseHeaders(200, 0);
    }
}
