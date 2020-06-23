package com.warehouse.Controller;

import com.warehouse.Model.User;
import com.warehouse.Service.UserService;
import org.apache.logging.log4j.LogManager;

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
}
