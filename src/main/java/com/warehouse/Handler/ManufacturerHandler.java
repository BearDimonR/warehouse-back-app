package com.warehouse.Handler;

import com.sun.jdi.LongValue;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.ManufacturerDAO;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Manufacturer;
import com.warehouse.Model.Role;
import com.warehouse.utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ManufacturerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getManufacturer(exchange);
                break;
            case "PUT":
                updateManufacturer(exchange);
                break;
            case "POST":
                createManufacturer(exchange);
                break;
            case "DELETE":
                deleteManufacturer(exchange);
                break;
            default:
                System.err.println("Undefined request method!");
        }
    }

    private void getManufacturer(HttpExchange exchange) throws IOException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        try {
            String json = "";
            if (id.isEmpty()) {
                List<Manufacturer> manufacturers = ManufacturerDAO.getInstance().getAll();
                json = JsonProceed.getGson().toJson(manufacturers);
            } else {
                Optional<Manufacturer> manufacturer = ManufacturerDAO.getInstance().get(Long.valueOf(id.get()));
                json = JsonProceed.getGson().toJson(manufacturer.get());
            }
            exchange.sendResponseHeaders(200, 0);
            //TODO Encrypt manufacturers
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting manufacturers streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting manufacturers");
        }
    }

    private void updateManufacturer(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Manufacturer manufacturer = JsonProceed.getGson().fromJson(new String(input), Manufacturer.class);
            if (!ManufacturerDAO.getInstance().update(manufacturer, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created manufacturer");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such manufacturer name already used!");
            } else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when editing manufacturer");
            }
            exchange.close();
        }
    }

    private void createManufacturer(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Manufacturer manufacturer = JsonProceed.getGson().fromJson(new String(input), Manufacturer.class);
            ManufacturerDAO.getInstance().save(manufacturer);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such manufacturer name already used!");
            } else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating manufacturer");
            }
            exchange.close();
        }
    }

    private void deleteManufacturer(HttpExchange exchange) throws IOException {
        try {
            Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
            //TODO decode input array
            if (!RoleDAO.getInstance().delete(Long.valueOf(id.get())))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting manufacturer");
        }
    }
}
