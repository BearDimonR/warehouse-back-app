package com.warehouse.Service;


import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.RolePermissionDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Permission;
import com.warehouse.Model.RolePermissionConnection;

import java.security.Provider;
import java.sql.SQLException;

import java.util.Arrays;
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

    public List<Permission> getAllRolePermissions(long id, PageFilter pageFilter, OrderBy order) throws SQLException {
        List<Long> ids =
                RolePermissionDAO.getInstance().get(id).stream().collect(Collectors.toList());
        return PermissionDAO.getInstance().getAll(
                Filter.builder()
                        .ids(ids)
                        .build(), pageFilter, order);
    }

    public long create(RolePermissionConnection t) throws SQLException {
        return RolePermissionDAO.getInstance().create(t);
    }

    public boolean delete(RolePermissionConnection t) throws SQLException {
        return RolePermissionDAO.getInstance().delete(t);
    }

    public int count(long id) throws SQLException {
        return RolePermissionDAO.getInstance().get(id).size();
    }
}
