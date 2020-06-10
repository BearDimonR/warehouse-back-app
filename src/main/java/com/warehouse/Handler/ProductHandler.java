package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.ProductDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Product;
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

public class ProductHandler extends AbstractHandler {

    public ProductHandler() {
        getPermission = "product_read";
        updatePermission = "product_edit";
        createPermission = "product_create";
        deletePermission = "product_delete";

        logger = LogManager.getLogger(ProductHandler.class);
        model = Product.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        String json;
        if (params.isEmpty()) {
            List<Product> products = ProductDAO.getInstance().getAll();
            json = JsonProceed.getGson().toJson(products);
        } else {
            Optional<Product> product = ProductDAO.getInstance().get(Long.parseLong(params.get("id")));
            if (product.isEmpty())
                throw new InvalidParameterException();
            json = JsonProceed.getGson().toJson(product.get());
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
        Product product = JsonProceed.getGson().fromJson(new String(input), Product.class);
        if (!ProductDAO.getInstance().update(product, null))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, 0);
    }

    @Override
    protected void create(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        // decode input array
        Product product = JsonProceed.getGson().fromJson(new String(input), Product.class);
        long id = ProductDAO.getInstance().save(product);
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(String.valueOf(id).getBytes());
        os.flush();
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if (!ProductDAO.getInstance().delete(Long.parseLong(params.get("id"))))
            throw new InvalidParameterException();
        else
            exchange.sendResponseHeaders(200, 0);
    }

}
