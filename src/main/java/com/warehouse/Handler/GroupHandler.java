package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.GroupDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Group;
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

public class GroupHandler extends AbstractHandler {

    public GroupHandler() {
        getPermission = "";
        updatePermission = "";
        createPermission = "";
        deletePermission = "";

        logger = LogManager.getLogger(GroupHandler.class);
        model = Group.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        String json;
        if (params.isEmpty()) {
            List<Group> groups = GroupDAO.getInstance().getAll();
            json = JsonProceed.getGson().toJson(groups);
        }
        else {
            Optional<Group> group = GroupDAO.getInstance().get(Long.parseLong(params.get("id")));
            if (group.isEmpty())
                throw new InvalidParameterException();
            json = JsonProceed.getGson().toJson(group.get());
        }
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        //Encrypt roles
        os.write(json.getBytes());
        os.flush();
    }

    @Override
    protected void update(HttpExchange exchange) throws IOException, InvalidParameterException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        // decode input array
        Group group = JsonProceed.getGson().fromJson(new String(input), Group.class);
        if (!GroupDAO.getInstance().update(group, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Group group = JsonProceed.getGson().fromJson(new String(input), Group.class);
            if(!GroupDAO.getInstance().save(group))
                throw new InvalidParameterException();
            exchange.sendResponseHeaders(200, -1);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!GroupDAO.getInstance().delete(Long.parseLong(params.get("id"))))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }
}
