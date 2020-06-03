package com.warehouse.DAO;

import com.warehouse.Model.Role;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class RoleDAO implements DAO<Role> {

    public static RoleDAO instance;

    public synchronized static RoleDAO getInstance() {
        if(instance == null)
            instance = new RoleDAO();
        return instance;
    }

    Connection connection;

    private RoleDAO() {}

    @Override
    public Optional<Role> get(long id) {
        connection = DataBaseConnector.getInstance().getConnection();
        //TODO SOME ACTIONS
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return Optional.empty();
    }

    @Override
    public List<Role> getAll() {
        return null;
    }

    @Override
    public void save(Role role) {

    }

    @Override
    public void update(Role role, String[] params) {

    }

    @Override
    public void delete(Role role) {

    }
}
