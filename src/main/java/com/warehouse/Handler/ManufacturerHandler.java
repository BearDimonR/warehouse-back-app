package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.ManufacturerDAO;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Manufacturer;
import com.warehouse.utils.QueryParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManufacturerHandler implements HttpHandler {

    Logger manufacturerLogger = LogManager.getLogger(ManufacturerHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
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
                manufacturerLogger.error("Undefined request method: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(400, -1);
        }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            manufacturerLogger.error("Problem with manufacturer streams\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            manufacturerLogger.error("Trying to access manufacturer with wrong id");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                manufacturerLogger.error("Not unique manufacturer name\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                manufacturerLogger.error("Problem with server response\n\t" + e.getMessage());
            }
        } catch (Exception e) {
            manufacturerLogger.error("Undefined exception\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getManufacturer(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
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
    }

    private void updateManufacturer(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Manufacturer manufacturer = JsonProceed.getGson().fromJson(new String(input), Manufacturer.class);
            if (!ManufacturerDAO.getInstance().update(manufacturer, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }

    private void createManufacturer(HttpExchange exchange) throws IOException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            //TODO decode input array
            Manufacturer manufacturer = JsonProceed.getGson().fromJson(new String(input), Manufacturer.class);
            ManufacturerDAO.getInstance().save(manufacturer);
            exchange.sendResponseHeaders(200, -1);

    }

    private void deleteManufacturer(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
            //TODO decode input array
            if (!RoleDAO.getInstance().delete(Long.valueOf(id.get())))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }
}
