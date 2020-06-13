package com.warehouse.Service;

import com.warehouse.DAO.RoleDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Role> get(long id) throws SQLException {
        Optional<Role> role = dao.get(id);
        if (role.isPresent()) {
            role.get().setUserNumber(UserService.getInstance().getUsersCountByRole(role.get().getId()));
        }
        return role;
    }

    @Override
    public List<Role> getAll(Filter filter, PageFilter pageFilter) throws SQLException {
        List<Role> roles = dao.getAll(filter, pageFilter);
        for (Role role : roles) {
            role.setUserNumber(UserService.getInstance().getUsersCountByRole(role.getId()));
        }
        return roles;
    }
}
