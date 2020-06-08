package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.warehouse.DAO.ProductDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Product;
import com.warehouse.Utils.QueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
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
                System.err.println("Undefined request method!");
        }
    }

    private void getProduct(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
        if(params.isEmpty())
            getAllProducts(exchange);
        else
            getProduct(exchange, Long.parseLong(params.get("id")));
    }

    private void getAllProducts(HttpExchange exchange) throws IOException {
        try {
            List<Product> products = ProductDAO.getInstance().getAll();
            OutputStream os = exchange.getResponseBody();
            String productJson = JsonProceed.getGson().toJson(products);
            exchange.sendResponseHeaders(200, 0);
            //Encrypt Product
            os.write(productJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting Product streams!");
            throw e;
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting Product");
        }
    }

    private void getProduct(HttpExchange exchange, long id) throws IOException {
        try {
            Optional<Product> product = ProductDAO.getInstance().get(id);
            if (product.isEmpty())
                throw new InvalidParameterException();
            OutputStream os = exchange.getResponseBody();
            String ProductJson = JsonProceed.getGson().toJson(product.get());
            exchange.sendResponseHeaders(200, 0);
            //Encrypt Product
            os.write(ProductJson.getBytes());
            os.flush();
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with getting Product streams!");
            throw e;
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created Product");
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when getting Product");
        }
    }

    private void postProduct(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Product product = JsonProceed.getGson().fromJson(new String(input), Product.class);
            ProductDAO.getInstance().save(product);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            // check if exception about unique name
            e.printStackTrace();
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such Product name already used!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when creating Product");
            }
            exchange.close();
        }
    }

    private void putProduct(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            byte[] input = is.readAllBytes();
            // decode input array
            Product product = JsonProceed.getGson().fromJson(new String(input), Product.class);
            if (!ProductDAO.getInstance().update(product, null))
                throw new InvalidParameterException();
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (InvalidParameterException e) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            System.err.println("Trying to access not created Product");
        } catch (SQLException e) {
            if(e.getSQLState().equals("23505")) {
                exchange.sendResponseHeaders(409, 0);
                System.err.println("Such Product name already used!");
            }
            else {
                exchange.sendResponseHeaders(500, 0);
                System.err.println("Problem with server response when editing Product");
            }
            exchange.close();
        }
    }

    private void deleteProduct(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = QueryParser.parse(exchange.getRequestURI().getQuery());
            if(!ProductDAO.getInstance().delete(Long.parseLong(params.get("id"))))
                exchange.sendResponseHeaders(404, 0);
            else
                exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            System.err.println("Problem with server response when deleting Product");
        } catch (NullPointerException e) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            System.err.println("Null pointer in getting id");
        }
    }
    
}
