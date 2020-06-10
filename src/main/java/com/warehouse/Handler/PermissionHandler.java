package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.PermissionDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Permission;
import com.warehouse.Utils.QueryParser;
import org.apache.logging.log4j.LogManager;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PermissionHandler extends AbstractHandler {

    public PermissionHandler() {
        getPermission = "permission_read";
        updatePermission = "permission_edit";
        createPermission = "permission_create";
        deletePermission = "permission_delete";

        logger = LogManager.getLogger(PermissionHandler.class);
        model = Permission.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        String json;
        if (params.isEmpty()) {
            List<Permission> permissions = PermissionDAO.getInstance().getAll();
            json = JsonProceed.getGson().toJson(permissions);
        }
        else {
            Optional<Permission> permission = PermissionDAO.getInstance().get(Long.parseLong(params.get("id")));
            if (permission.isEmpty())
                throw new InvalidParameterException();
            json = JsonProceed.getGson().toJson(permission.get());
        }
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        //Encrypt roles
        os.write(json.getBytes());
        os.flush();
    }


    @Override
    protected void update(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Permission permission = JsonProceed.getGson().fromJson(new String(input), Permission.class);
            if (!PermissionDAO.getInstance().update(permission, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
    }

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Permission permission = JsonProceed.getGson().fromJson(new String(input), Permission.class);
            if(!PermissionDAO.getInstance().save(permission))
                throw new InvalidParameterException();
            exchange.sendResponseHeaders(200, 0);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
            //TODO decode input array
            if (!PermissionDAO.getInstance().delete(Long.parseLong(id.get())))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
    }
}
