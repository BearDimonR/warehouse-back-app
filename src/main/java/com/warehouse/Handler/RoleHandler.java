package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;

import com.warehouse.DAO.RoleDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Role;
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

public class RoleHandler extends AbstractHandler {

    public RoleHandler() {
        getPermission = "role_read";
        updatePermission = "role_edit";
        createPermission = "role_create";
        deletePermission = "role_delete";

        logger = LogManager.getLogger(RoleHandler.class);
        model = Role.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        String json;
        if (params.isEmpty()) {
            List<Role> roles = RoleDAO.getInstance().getAll();
            json = JsonProceed.getGson().toJson(roles);
        }
        else {
            Optional<Role> role = RoleDAO.getInstance().get(Long.parseLong(params.get("id")));
            if (role.isEmpty())
                throw new InvalidParameterException();
            json = JsonProceed.getGson().toJson(role.get());
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
        // decode input array
        Role role = JsonProceed.getGson().fromJson(new String(input), Role.class);
        if (!RoleDAO.getInstance().update(role, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, 0);
    }

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        // decode input array
        Role role = JsonProceed.getGson().fromJson(new String(input), Role.class);
        if(!RoleDAO.getInstance().save(role))
            throw new InvalidParameterException();
        exchange.sendResponseHeaders(200, 0);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if (!RoleDAO.getInstance().delete(Long.parseLong(params.get("id"))))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, 0);
    }
}
