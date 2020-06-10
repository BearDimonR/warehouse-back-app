package com.warehouse.Handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.UserDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.User;
import com.warehouse.Model.auth.AuthenticatedUserDTO;
import com.warehouse.Model.auth.Credentials;
import com.warehouse.Authentication.Authentication;
import com.warehouse.exceptions.AuthWrongException;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Optional;

public class LoginHandler extends AbstractHandler {

    public LoginHandler() {
        logger = LogManager.getLogger(LoginHandler.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            logger.info("Login request: " + exchange.getRequestMethod());
            enableCORS(exchange);
            switch (exchange.getRequestMethod()) {
                case "POST":
                    login(exchange);
                    break;
                case "OPTIONS":
                    options(exchange);
                    break;
                default:
                    logger.warn("Undefined request method!");
                    exchange.sendResponseHeaders(400, 0);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
            logger.error("Problem with login streams.\n\t" + e.getMessage());
        } catch (AuthWrongException e) {
            exchange.sendResponseHeaders(401, 0);
            logger.error("Wrong login information.\n\t");
        } catch (SQLException e) {
            exchange.sendResponseHeaders(500, 0);
            logger.error("Problem with server response.\n\t" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void login(HttpExchange exchange) throws IOException, SQLException, AuthWrongException {
        byte[] input = exchange.getRequestBody().readAllBytes();
        //TODO decode input array
        Optional<User> userByCredentials = UserDAO.getInstance().getByCredentials(JsonProceed.getGson().fromJson(new String(input), Credentials.class));
        if (userByCredentials.isPresent()) {
            Optional<AuthenticatedUserDTO> user = Authentication.generateLoginResponse(userByCredentials.get());
            if (user.isPresent()) {
                byte[] response = new Gson().toJson(user.get()).getBytes();
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }
            //else throw new AuthWrongException();
        } else
            throw new AuthWrongException();

        logger.info("Successful logged in: " + userByCredentials.get().getName()
                + " role: " + userByCredentials.get().getRoleId());
    }
}
