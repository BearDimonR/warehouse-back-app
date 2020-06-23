package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Authentication.Authentication;
import com.warehouse.Model.User;
import com.warehouse.Service.UserService;
import com.warehouse.Utils.JsonProceed;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class UserController extends AbstractController<User> {

    public UserController() {
        super(User.class);
        viewPermissions = new ArrayList<>(Arrays.asList("user_page_view"));
        getPermission = "user_read";
        updatePermission = "user_edit";
        createPermission = "user_create";
        deletePermission = "user_delete";
        service = UserService.getInstance();

        logger = LogManager.getLogger(UserController.class);
    }

    @Override
    protected Object create(HttpExchange exchange)
            throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        byte[] input = is.readAllBytes();
        User user = JsonProceed.getGson().fromJson(new String(input), User.class);
        user.setPassword(Authentication.encodePasswordMD5(user.getPassword()));
        return service.create(user);
    }

}
