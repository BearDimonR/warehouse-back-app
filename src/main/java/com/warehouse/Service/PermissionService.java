package com.warehouse.Service;

import com.warehouse.DAO.PermissionDAO;
import com.warehouse.Model.Permission;

public class PermissionService extends BasicService<Permission> {
    public static PermissionService instance;

    public synchronized static PermissionService getInstance() {
        if (instance == null)
            instance = new PermissionService();
        return instance;
    }

    private PermissionService() {
        dao = PermissionDAO.getInstance();
    }
}
