package com.warehouse.Controller;

import com.warehouse.DAO.RoleDAO;
import com.warehouse.Model.Role;
import org.apache.logging.log4j.LogManager;

public class RoleController extends AbstractController<Role> {

    public RoleController() {
        super(Role.class);
        getPermission = "role_read";
        updatePermission = "role_edit";
        createPermission = "role_create";
        deletePermission = "role_delete";
        dao = RoleDAO.getInstance();

        logger = LogManager.getLogger(RoleController.class);
    }
}
