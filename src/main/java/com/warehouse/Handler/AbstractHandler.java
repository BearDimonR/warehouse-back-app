package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.Authentication.Authentication;
import com.warehouse.exceptions.AuthRequiredException;
import com.warehouse.exceptions.NoPermissionException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.sql.SQLException;

abstract class AbstractHandler implements HttpHandler, CORSEnabled {

    protected String getPermission = "";
    protected String updatePermission = "";
    protected String createPermission = "";
    protected String deletePermission = "";
    protected Logger logger;
    protected Type model;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        enableCORS(exchange);
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (Authentication.hasPermission(exchange, getPermission))
                        get(exchange);
                    break;
                case "PUT":
                    if (Authentication.hasPermission(exchange, updatePermission))
                        update(exchange);
                    break;
                case "POST":
                    if (Authentication.hasPermission(exchange, createPermission))
                        create(exchange);
                    break;
                case "DELETE":
                    if (Authentication.hasPermission(exchange, deletePermission))
                        delete(exchange);
                    break;
                default:
                    logger.error("Undefined request method: " + exchange.getRequestMethod() + ".");
                    exchange.sendResponseHeaders(400, -1);
            }
        }catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            logger.error("Problem with" + model + "streams.\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            logger.error("Trying to access " + model + " with wrong id.");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                logger.error("Unique problem " + model + " can be name or id...\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                logger.error("Problem with server response.\n\t" + e.getMessage());
            }
        } catch (NoPermissionException e) {
            exchange.sendResponseHeaders(403, -1);
            logger.error("Not enough permissions.\n\t" + e.getMessage());
        } catch (AuthRequiredException e) {
            exchange.sendResponseHeaders(401, -1);
            logger.error("Authentication failed.\n\t" + e.getMessage());
        } catch (Exception e) {
            logger.error("Undefined exception.\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {}
    protected void create(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {}
    protected void update(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {}
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {}
}
