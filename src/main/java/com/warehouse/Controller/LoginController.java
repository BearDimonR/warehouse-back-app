package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Authentication.Authentication;
import com.warehouse.Exception.AuthWrongException;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Http.Response;
import com.warehouse.Model.*;
import com.warehouse.Model.auth.AuthenticatedUserDTO;
import com.warehouse.Model.auth.Credentials;
import com.warehouse.Service.UserService;
import com.warehouse.Utils.JsonProceed;
import com.warehouse.Utils.QueryParser;
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
                    resultBody = receiveToken(exchange);
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

    private synchronized Object receiveToken(HttpExchange exchange)
            throws IOException, SQLException, AuthWrongException {
        if (QueryParser.parse(exchange.getRequestURI().getQuery()).get("renovation").equals("true"))
            return renovate(exchange);
        else
            return login(exchange);

    }

    private Object login(HttpExchange exchange) throws IOException, SQLException, AuthWrongException {
        byte[] input = exchange.getRequestBody().readAllBytes();
        //TODO decode input array
        var credentials = JsonProceed.getGson().fromJson(new String(input), Credentials.class);
        if (credentials == null)
            throw new AuthWrongException();
        credentials.setPassword(Authentication.encodePasswordMD5(credentials.getPassword()));
        Optional<User> userByCredentials = UserService.getInstance().getByCredentials(credentials);
        if (userByCredentials.isPresent()) {
            Optional<AuthenticatedUserDTO> user = Authentication.generateLoginResponse(userByCredentials.get());
            if (user.isPresent()) {
                logger.info("Successful logged in: " + userByCredentials.get().getName()
                        + " role: " + userByCredentials.get().getRoleId());
                return user.get();
            }
        } else
            throw new AuthWrongException();
        return ResponseMessage.of("OK");
    }

    private Object renovate(HttpExchange exchange) throws IOException, AuthWrongException {
        byte[] input = exchange.getRequestBody().readAllBytes();
        //TODO decode input array
        String token = JsonProceed.getGson().fromJson(new String(input), RenovationToken.class).getRenovateToken();
        Optional<TokenRenovationDTO> renovationDTO = Authentication.generateRenovationResponse(token);
        if (renovationDTO.isPresent()) {
            logger.info("Successful renovated token");
            return renovationDTO.get();
        }
        return ResponseMessage.of("Wrong auth info");
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
