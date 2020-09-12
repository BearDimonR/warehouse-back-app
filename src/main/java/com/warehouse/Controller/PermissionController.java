package com.warehouse.Controller;

import com.warehouse.Model.Permission;
import com.warehouse.Service.PermissionService;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;


public class PermissionController extends AbstractController<Permission> {

    public PermissionController() {
        super(Permission.class);
        viewPermissions = new ArrayList<>(Arrays.asList("permission_page_view","role_page_view"));
        getPermission = "permission_read";
        updatePermission = "permission_edit";
        createPermission = "permission_create";
        deletePermission = "permission_delete";
        service = PermissionService.getInstance();

        logger = LogManager.getLogger(PermissionController.class);
    }
}
