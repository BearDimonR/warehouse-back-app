package com.warehouse.DAO;

import com.warehouse.Model.Permission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionDAO implements DAO<Permission> {

    public static PermissionDAO instance;

    public synchronized static PermissionDAO getInstance() {
        if (instance == null)
            instance = new PermissionDAO();
        return instance;
    }

    Connection connection;

    private PermissionDAO() {
    }


    @Override
    public Optional<Permission> get(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM permission WHERE id = ?");
        preparedStatement.setLong(1, id);
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        if (res.next())
            return Optional.of(new Permission(
                    res.getLong(1),
                    res.getString(2),
                    res.getBoolean(3)));
        return Optional.empty();
    }

    @Override
    public synchronized List<Permission> getAll() throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM permission");
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        List<Permission> permissions = new ArrayList<>();
        while(res.next()) {
            permissions.add(new Permission(res.getLong(1), res.getString(2), res.getBoolean(3)));
        }
        return permissions;
    }

    @Override
    public synchronized boolean save(Permission permission) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO permission (name , is_super) VALUES  (?,?)");

        preparedStatement.setString(1, permission.getName());
        preparedStatement.setBoolean(2, permission.isSuper());

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean update(Permission permission, String[] params) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE permission SET name=?,is_super=? WHERE id=?");
        preparedStatement.setString(1, permission.getName());
        preparedStatement.setBoolean(2, permission.isSuper());
        preparedStatement.setLong(3, permission.getId());

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM permission WHERE id=?");
        preparedStatement.setLong(1, id);

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }
}