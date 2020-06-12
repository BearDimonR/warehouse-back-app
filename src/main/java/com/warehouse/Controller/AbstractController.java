package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.Authentication.Authentication;
import com.warehouse.Exception.AuthRequiredException;
import com.warehouse.Exception.NoPermissionException;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Filter.Filter;
import com.warehouse.Http.Response;
import com.warehouse.Model.ResponseMessage;
import com.warehouse.Service.Service;
import com.warehouse.Utils.JsonProceed;
import com.warehouse.Utils.QueryParser;
import com.warehouse.View.View;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractController<T> implements HttpHandler, CORSEnabled {

    protected String getPermission = "";
    protected String updatePermission = "";
    protected String createPermission = "";
    protected String deletePermission = "";
    protected Logger logger;
    protected Service<T> service;

    private Class<T> model;

    protected static View view;

    public AbstractController(Class<T> model) {
        this.model = model;
    }

    public static void setView(View view) {
        AbstractController.view = view;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("Got request: " + exchange.getRequestMethod());
        Object resultBody = "Something goes wrong";
        int status = 200;
        try {
            enableCORS(exchange);
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (Authentication.hasPermission(exchange, getPermission))
                        resultBody = get(exchange);
                    break;
                case "PUT":
                    if (Authentication.hasPermission(exchange, updatePermission))
                        resultBody = update(exchange);
                    break;
                case "POST":
                    if (Authentication.hasPermission(exchange, createPermission))
                        resultBody = create(exchange);
                    break;
                case "DELETE":
                    if (Authentication.hasPermission(exchange, deletePermission))
                        resultBody = delete(exchange);
                    break;
                case "OPTIONS":
                    resultBody = options(exchange);
                    break;
                default:
                    resultBody = "Undefined request method";
                    status = 400;
                    logger.error("Undefined request method: " + exchange.getRequestMethod() + ".");
            }
        } catch (IOException e) {
            resultBody = ResponseMessage.of("Server IOException");
            status = 500;
            logger.error("Problem with" + model.getName() + "streams.\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            resultBody = ResponseMessage.of("Wrong request id");
            status = 404;
            logger.error("Trying to access " + model.getName() + " with wrong id.");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                resultBody = ResponseMessage.of("Uniqueness problem");
                status = 409;
                logger.error(resultBody + model.getName() + " can be name or id...\n\t" + e.getMessage());
            } else {
                resultBody = ResponseMessage.of("Server SQLException");
                status = 500;
                logger.error(resultBody + "\n\t" + e.getMessage());
            }
        } catch (NoPermissionException e) {
            resultBody = ResponseMessage.of("No permission");
            status = 403;
            logger.error("Not enough permissions.\n\t" + e.getMessage());
        } catch (AuthRequiredException e) {
            resultBody = ResponseMessage.of("Authentication required");
            status = 401;
            logger.error("Authentication failed.\n\t" + e.getMessage());
        } catch (Exception e) {
            resultBody = ResponseMessage.of("Unexpected error occurred");
            status = 500;
            logger.error("Undefined exception.\n\t" + e.getMessage());
            e.printStackTrace();
        } finally {
            view.view(Response.of(
                    resultBody,
                    status,
                    exchange));
            logger.info("Finish request: " + exchange.getRequestMethod() + " with " + exchange.getResponseCode());
            exchange.close();
        }
    }

    protected String options(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        return "Success";
    }

    protected Object get(HttpExchange exchange)
            throws SQLException, InvalidParameterException, NotImplementedException, IOException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if (params.containsKey("filter")) {
            Filter obj;
            if (params.get("filter").equals("undefined"))
                obj = new Filter();
            else
                obj = JsonProceed.getGson().fromJson(params.get("filter"), Filter.class);
            if (obj.isCount())
                return service.count(obj);
            return service.getAll(obj);
        } else {
            Optional<T> optional = service.get(Long.parseLong(params.get("id")));
            if (optional.isEmpty())
                throw new InvalidParameterException();
            return optional.get();
        }
    }

    protected Object create(HttpExchange exchange)
            throws IOException, SQLException, NotImplementedException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        T obj = JsonProceed.getGson().fromJson(new String(input), model);
        return service.create(obj);
    }

    protected Object update(HttpExchange exchange)
            throws IOException, SQLException, InvalidParameterException, NotImplementedException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        T obj = JsonProceed.getGson().fromJson(new String(input), model);
        if (!service.update(obj))
            throw new InvalidParameterException();
        else
            return obj;
    }

    protected Object delete(HttpExchange exchange)
            throws SQLException, InvalidParameterException, NotImplementedException {
        long id = Long.parseLong(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        if (!service.delete(id))
            throw new InvalidParameterException();
        else
            return id;
    }
}
