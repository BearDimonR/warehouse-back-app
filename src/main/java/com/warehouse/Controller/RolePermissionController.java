package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Model.RolePermissionConnection;
import com.warehouse.Service.RolePermissionService;
import com.warehouse.Utils.QueryParser;
import org.apache.logging.log4j.LogManager;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Map;

public class RolePermissionController extends AbstractController<RolePermissionConnection> {

    public RolePermissionController() {
        super(RolePermissionConnection.class);
        getPermission = "read_permission_role_connection";
        updatePermission = "";
        createPermission = "assign_permission_to_role";
        deletePermission = "unassign_permission_from_role";

        logger = LogManager.getLogger(RolePermissionController.class);
    }

    @Override
    protected Object get(HttpExchange exchange) throws SQLException {
        return RolePermissionService.getInstance().getAllRolePermissions(
                Long.parseLong(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id")));
    }

    @Override
    protected Object update(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    protected Object delete(HttpExchange exchange) throws SQLException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        RolePermissionConnection rpc = new RolePermissionConnection(
                Long.parseLong(params.get("roleId")),
                Long.parseLong(params.get("permissionId")));
        if (!RolePermissionService.getInstance().delete(rpc))
            throw new InvalidParameterException();
        else
            return rpc;
    }
}
