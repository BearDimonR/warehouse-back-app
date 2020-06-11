package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Authentication.Authentication;
import com.warehouse.DAO.UserDAO;
import com.warehouse.Exception.AuthWrongException;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Http.Response;
import com.warehouse.Model.AuthenticatedUserDTO;
import com.warehouse.Model.Credentials;
import com.warehouse.Model.ResponseMessage;
import com.warehouse.Model.User;
import com.warehouse.Utils.JsonProceed;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController extends AbstractController<Credentials> {

    public LoginController() {
        super(Credentials.class);
        logger = LogManager.getLogger(LoginController.class);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Object resultBody = "Something goes wrong";
        int status = 200;
        try {
            logger.info("Login request: " + exchange.getRequestMethod());
            enableCORS(exchange);
            switch (exchange.getRequestMethod()) {
                case "POST":
                    resultBody = login(exchange);
                    break;
                case "OPTIONS":
                    resultBody = options(exchange);
                    break;
                default:
                    resultBody = "Undefined request method";
                    status = 400;
                    logger.error("Undefined request method: " + exchange.getRequestMethod() + ".");            }
        } catch (IOException e) {
            resultBody = ResponseMessage.of("Server IOException");
            status = 500;
            logger.error("Problem with" + Credentials.class + "streams.\n\t" + e.getMessage());
        } catch (AuthWrongException e) {
            exchange.sendResponseHeaders(401, 0);
            resultBody = ResponseMessage.of("Wrong auth information");
            status = 500;
            logger.error(resultBody);
        } catch (SQLException e) {
            resultBody = ResponseMessage.of("Server SQLException");
            status = 500;
            logger.error(resultBody + "\n\t" + e.getMessage());
        } finally {
            view.view(Response.of(
                    resultBody,
                    status,
                    exchange));
            logger.info("Finish request: " + exchange.getRequestMethod() + " with " + exchange.getResponseCode());
            exchange.close();
        }
    }

    private Object login(HttpExchange exchange) throws IOException, SQLException, AuthWrongException {
        byte[] input = exchange.getRequestBody().readAllBytes();
        //TODO decode input array
        Optional<User> userByCredentials = UserDAO.getInstance().getByCredentials(
                JsonProceed.getGson().fromJson(new String(input), Credentials.class));
        if (userByCredentials.isPresent()) {
            Optional<AuthenticatedUserDTO> user = Authentication.generateLoginResponse(userByCredentials.get());
            if (user.isPresent()) {
                logger.info("Successful logged in: " + userByCredentials.get().getName()
                        + " role: " + userByCredentials.get().getRoleId());
                return user.get();
            }
        } else
            throw new AuthWrongException();
        //TODO WHY IS THAT HAPPENING!!!
        return ResponseMessage.of("WTF");
    }

    @Override
    protected Object get(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    protected Object create(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    protected Object update(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    protected Object delete(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }
}
