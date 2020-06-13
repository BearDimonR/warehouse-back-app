package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.RolePermissionConnection;
import com.warehouse.Service.RolePermissionService;
import com.warehouse.Utils.JsonProceed;
import com.warehouse.Utils.QueryParser;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
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
        Map<String, String> map = QueryParser.parse(exchange.getRequestURI().getQuery());
        PageFilter pageFilter = new PageFilter();
        if(map.containsKey("page")) {
            pageFilter = JsonProceed.getGson().fromJson(map.get("page"), PageFilter.class);
        }
        if(map.containsKey("filter")) {
            Filter filter = JsonProceed.getGson().fromJson(map.get("filter"), Filter.class);
            if(filter.isCount())
                return RolePermissionService.getInstance().count(
                        Long.parseLong(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id")));
        }
        return RolePermissionService.getInstance().getAllRolePermissions(
                Long.parseLong(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id")), pageFilter);
    }

    @Override
    protected Object create(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        RolePermissionConnection obj = JsonProceed.getGson().fromJson(new String(input), RolePermissionConnection.class);
        return RolePermissionService.getInstance().create(obj);
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
