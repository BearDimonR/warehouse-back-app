package com.warehouse.Service;


import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.RolePermissionDAO;
import com.warehouse.Filter.GeneralFilter;
import com.warehouse.Model.Permission;

import java.sql.SQLException;
import java.util.List;

public class RolePermissionService {

    public static RolePermissionService instance;

    public synchronized static RolePermissionService getInstance() {
        if (instance == null)
            instance = new RolePermissionService();
        return instance;
    }

    private RolePermissionService() {
    }

    public List<Permission> getRolePermissions(long id) throws SQLException {
        List<Integer> ids = RolePermissionDAO.getInstance().get(id);
        return PermissionDAO.getInstance().getAll(0, 20, GeneralFilter.builder().ids(ids).build());
    }

}
