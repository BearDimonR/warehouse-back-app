package com.warehouse.DAO;


import com.warehouse.Model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDAO implements DAO<Role> {

    public static RoleDAO instance;

    public synchronized static RoleDAO getInstance() {
        if(instance == null)
            instance = new RoleDAO();
        return instance;
    }

    private Connection connection;

    private RoleDAO() {}

    @Override
    public Optional<Role> get(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM role WHERE id = ?");
        preparedStatement.setLong(1, id);
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        if(res.next())
            return Optional.of(new Role(
                res.getLong(1),
                res.getString(2),
                res.getBoolean(3)));
        return Optional.empty();
    }

    @Override
    public List<Role> getAll() throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM role");
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        List<Role> roles = new ArrayList<>();
        while (res.next()) {
            roles.add(new Role(
                    res.getLong(1),
                    res.getString(2),
                    res.getBoolean(3)));
        }
        return roles;
    }

    @Override
    public synchronized boolean save(Role role) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO role (name, is_super) VALUES (?,?)");
        preparedStatement.setString(1, role.getName());
        preparedStatement.setBoolean(2, role.is_super());
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean update(Role role, String[] params) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("UPDATE role SET name = ?, is_super = ? WHERE id = ?");
        preparedStatement.setString(1, role.getName());
        preparedStatement.setBoolean(2, role.is_super());
        preparedStatement.setLong(3, role.getId());
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM role WHERE id = ?");
        preparedStatement.setLong(1, id);
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }
}
