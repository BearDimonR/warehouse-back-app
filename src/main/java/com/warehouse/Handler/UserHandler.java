package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.UserDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.User;
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

public class UserHandler extends AbstractHandler {

    public UserHandler() {
        getPermission = "";
        updatePermission = "";
        createPermission = "";
        deletePermission = "";

        logger = LogManager.getLogger(UserHandler.class);
        model = User.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        String json;
        if (params.isEmpty()) {
            List<User> users = UserDAO.getInstance().getAll();
            json = JsonProceed.getGson().toJson(users);
        }
        else {
            Optional<User> user = UserDAO.getInstance().get(Long.parseLong(params.get("id")));
            if (user.isEmpty())
                throw new InvalidParameterException();
            json = JsonProceed.getGson().toJson(user.get());
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
        //TODO decode input array
        User user = JsonProceed.getGson().fromJson(new String(input), User.class);
        if (!UserDAO.getInstance().update(user, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        //TODO decode input array
        User user = JsonProceed.getGson().fromJson(new String(input), User.class);
        if(!UserDAO.getInstance().save(user))
            throw new InvalidParameterException();
        exchange.sendResponseHeaders(200, -1);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        //TODO decode input array
        if (!UserDAO.getInstance().delete(Long.parseLong(id.get())))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, -1);
    }
}
