package com.warehouse.Service;


import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.RolePermissionDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Permission;
import com.warehouse.Model.RolePermissionConnection;

import java.security.Provider;
import java.sql.SQLException;

import java.util.List;
import java.util.stream.Collectors;

public class RolePermissionService {

    public static RolePermissionService instance;

    public synchronized static RolePermissionService getInstance() {
        if (instance == null)
            instance = new RolePermissionService();
        return instance;
    }

    private RolePermissionService() {
    }

    public List<Permission> getAllRolePermissions(long id) throws SQLException {
        List<Long> ids =
                RolePermissionDAO.getInstance().get(id).stream().collect(Collectors.toList());
        return PermissionDAO.getInstance().getAll(
                Filter.builder()
                        .ids(ids)
                        .build(), new PageFilter());
    }

    public long create(RolePermissionConnection t) throws SQLException {
        return RolePermissionDAO.getInstance().save(t);
    }

    public boolean delete(RolePermissionConnection t) throws SQLException {
        return RolePermissionDAO.getInstance().delete(t);
    }

}
