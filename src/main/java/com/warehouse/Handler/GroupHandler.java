package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.GroupDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Group;
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

public class GroupHandler implements HttpHandler {

    Logger groupLogger = LogManager.getLogger(RolePermissionHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    getGroup(exchange);
                    break;
                case "PUT":
                    putGroup(exchange);
                    break;
                case "POST":
                    postGroup(exchange);
                    break;
                case "DELETE":
                    deleteGroup(exchange);
                    break;
                default:
                    groupLogger.error("Undefined request method: " + exchange.getRequestMethod());
                    exchange.sendResponseHeaders(400, -1);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            groupLogger.error("Problem with group streams\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            groupLogger.error("Trying to access group with wrong id");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                groupLogger.error("Not unique group name\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                groupLogger.error("Problem with server response\n\t" + e.getMessage());
            }
        } catch (Exception e) {
            groupLogger.error("Undefined exception\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getGroup(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if(params.isEmpty())
            getAllGroups(exchange);
        else
            getGroup(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllGroups(HttpExchange exchange) throws IOException, SQLException {
            List<Group> groups = GroupDAO.getInstance().getAll();
            OutputStream os = exchange.getResponseBody();
            String groupJson = JsonProceed.getGson().toJson(groups);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt groups
            os.write(groupJson.getBytes());
            os.flush();
    }

    private void getGroup(HttpExchange exchange, long id) throws IOException, InvalidParameterException, SQLException {
            Optional<Group> group = GroupDAO.getInstance().get(id);
            if (group.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            String groupJson = JsonProceed.getGson().toJson(group.get());
            exchange.sendResponseHeaders(200, 0);
            //Encrypt group
            os.write(groupJson.getBytes());
            os.flush();
    }

    private void postGroup(HttpExchange exchange) throws IOException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Group group = JsonProceed.getGson().fromJson(new String(input), Group.class);
            GroupDAO.getInstance().save(group);
            exchange.sendResponseHeaders(200, -1);
    }

    private void putGroup(HttpExchange exchange) throws IOException, InvalidParameterException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Group group = JsonProceed.getGson().fromJson(new String(input), Group.class);
            if (!GroupDAO.getInstance().update(group, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }

    private void deleteGroup(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!GroupDAO.getInstance().delete(Long.parseLong(params.get("id"))))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }
}
