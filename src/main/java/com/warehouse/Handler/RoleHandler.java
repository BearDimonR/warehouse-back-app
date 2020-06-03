package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.Model.Role;
import com.warehouse.Splitter;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Optional;

public class RoleHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()){
            case "GET":
                getRole(exchange);
                break;
            case "PUT":
                putRole(exchange);
                break;
            case "POST":
                postRole(exchange);
                break;
            case "DELETE":
                deleteRole(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }
    private void getRole(HttpExchange exchange) throws IOException {
        try {
            Optional<Role> role = RoleDAO.getInstance().get(Splitter.getId(exchange.getRequestURI()));
            if(role.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            // make role json and convert to bytes
            // os.write();
            os.flush();
            os.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(404, 0);
            System.err.println("Problem with getting role!");
            throw e;
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            System.err.println("Trying to access not created user");
        }
    }

    private void putRole(HttpExchange exchange) throws IOException {
    }

    private void postRole(HttpExchange exchange) throws IOException {

    }

    private void deleteRole(HttpExchange exchange) throws IOException {

    }





}
