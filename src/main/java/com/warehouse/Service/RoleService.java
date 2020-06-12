package com.warehouse.Service;

import com.warehouse.DAO.RoleDAO;
import com.warehouse.Model.Role;

public class RoleService extends BasicService<Role> {
    public static RoleService instance;

    public synchronized static RoleService getInstance() {
        if (instance == null)
            instance = new RoleService();
        return instance;
    }

    private RoleService() {
        dao = RoleDAO.getInstance();
    }
}
