package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.ProductDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Product;
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

public class ProductHandler implements HttpHandler {

    Logger productLogger = LogManager.getLogger(ProductHandler.class);


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    getProduct(exchange);
                    break;
                case "PUT":
                    putProduct(exchange);
                    break;
                case "POST":
                    postProduct(exchange);
                    break;
                case "DELETE":
                    deleteProduct(exchange);
                    break;
                default:
                    productLogger.error("Undefined request method: " + exchange.getRequestMethod());
                    exchange.sendResponseHeaders(400, -1);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
            productLogger.error("Problem with product streams\n\t" + e.getMessage());
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, -1);
            productLogger.error("Trying to access product with wrong id");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, -1);
                productLogger.error("Not unique product\n\t" + e.getMessage());
            } else {
                exchange.sendResponseHeaders(500, -1);
                productLogger.error("Problem with server response\n\t" + e.getMessage());
            }
        } catch (Exception e) {
            productLogger.error("Undefined exception\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getProduct(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if(params.isEmpty())
            getAllProducts(exchange);
        else
            getProduct(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllProducts(HttpExchange exchange) throws IOException, SQLException {
            List<Product> products = ProductDAO.getInstance().getAll();
            OutputStream os = exchange.getResponseBody();
            String productJson = JsonProceed.getGson().toJson(products);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt Product
            os.write(productJson.getBytes());
            os.flush();
    }

    private void getProduct(HttpExchange exchange, long id) throws IOException, InvalidParameterException, SQLException {
            Optional<Product> product = ProductDAO.getInstance().get(id);
            if (product.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            String ProductJson = JsonProceed.getGson().toJson(product.get());
            exchange.sendResponseHeaders(200, 0);
            //Encrypt Product
            os.write(ProductJson.getBytes());
            os.flush();
    }

    private void postProduct(HttpExchange exchange) throws IOException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Product product = JsonProceed.getGson().fromJson(new String(input), Product.class);
            ProductDAO.getInstance().save(product);
            exchange.sendResponseHeaders(200, -1);
    }

    private void putProduct(HttpExchange exchange) throws IOException, InvalidParameterException, SQLException {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Product product = JsonProceed.getGson().fromJson(new String(input), Product.class);
            if (!ProductDAO.getInstance().update(product, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }

    private void deleteProduct(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!ProductDAO.getInstance().delete(Long.parseLong(params.get("id"))))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, -1);
    }
    
}
