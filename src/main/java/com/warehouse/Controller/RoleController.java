package com.warehouse.Controller;

import com.warehouse.Model.Role;
import com.warehouse.Service.RoleService;
import org.apache.logging.log4j.LogManager;

public class RoleController extends AbstractController<Role> {

    public RoleController() {
        super(Role.class);
        getPermission = "role_read";
        updatePermission = "role_edit";
        createPermission = "role_create";
        deletePermission = "role_delete";
        service = RoleService.getInstance();

        logger = LogManager.getLogger(RoleController.class);
    }
}
