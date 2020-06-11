package com.warehouse.Controller;

import com.warehouse.DAO.UserDAO;
import com.warehouse.Model.User;
import org.apache.logging.log4j.LogManager;


public class UserController extends AbstractController<User> {

    public UserController() {
        super(User.class);
        getPermission = "user_read";
        updatePermission = "user_edit";
        createPermission = "user_create";
        deletePermission = "user_delete";
        dao = UserDAO.getInstance();

        logger = LogManager.getLogger(UserController.class);
    }
}
