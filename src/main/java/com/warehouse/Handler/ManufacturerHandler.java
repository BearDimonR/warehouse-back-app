package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.ManufacturerDAO;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Manufacturer;
import com.warehouse.Utils.QueryParser;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class ManufacturerHandler extends AbstractHandler {

    public ManufacturerHandler() {
        getPermission = "manufacturer_read";
        updatePermission = "manufacturer_edit";
        createPermission = "manufacturer_create";
        deletePermission = "manufacturer_edit";

        logger = LogManager.getLogger(ManufacturerHandler.class);
        model = Manufacturer.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        String json;
        if (id.isEmpty()) {
            List<Manufacturer> manufacturers = ManufacturerDAO.getInstance().getAll();
            json = JsonProceed.getGson().toJson(manufacturers);
        } else {
            Optional<Manufacturer> manufacturer = ManufacturerDAO.getInstance().get(Long.parseLong(id.get()));
            json = JsonProceed.getGson().toJson(manufacturer.get());
        }
        exchange.sendResponseHeaders(200, 0);
        //TODO Encrypt manufacturers
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes());
        os.flush();
    }

    @Override
    protected void update(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        //TODO decode input array
        Manufacturer manufacturer = JsonProceed.getGson().fromJson(new String(input), Manufacturer.class);
        if (!ManufacturerDAO.getInstance().update(manufacturer, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, 0);
    }

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        //TODO decode input array
        Manufacturer manufacturer = JsonProceed.getGson().fromJson(new String(input), Manufacturer.class);
        long id = ManufacturerDAO.getInstance().save(manufacturer);
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(String.valueOf(id).getBytes());
        os.flush();
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Optional<String> id = Optional.ofNullable(QueryParser.parse(exchange.getRequestURI().getQuery()).get("id"));
        //TODO decode input array
        if (!RoleDAO.getInstance().delete(Long.valueOf(id.get())))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, 0);
    }
}
