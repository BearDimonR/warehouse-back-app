package com.warehouse.Controller;

import com.warehouse.DAO.PermissionDAO;
import com.warehouse.Model.Permission;
import org.apache.logging.log4j.LogManager;


public class PermissionController extends AbstractController<Permission> {

    public PermissionController() {
        super(Permission.class);
        getPermission = "permission_read";
        updatePermission = "permission_edit";
        createPermission = "permission_create";
        deletePermission = "permission_delete";
        dao = PermissionDAO.getInstance();

        logger = LogManager.getLogger(PermissionController.class);
    }
}
