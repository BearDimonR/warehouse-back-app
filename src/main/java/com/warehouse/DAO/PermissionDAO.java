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
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM permission WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new Permission(
                        res.getLong(1),
                        res.getString(2),
                        res.getBoolean(3)));
            return Optional.empty();
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    public Optional<List<Permission>> getUsersPermissions(Long userId) throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM permission JOIN role_permission_connection ON permission.id = role_permission_connection.permission_id WHERE role_id = (SELECT role_id FROM user_account WHERE id = ?)");
            preparedStatement.setLong(1, userId);
            ResultSet res = preparedStatement.executeQuery();
            List<Permission> permissions = new ArrayList<>();
            while (res.next()) {
                permissions.add(new Permission(
                        res.getLong(1),
                        res.getString(2),
                        res.getBoolean(3)));
            }
            return Optional.of(permissions);
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public List<Permission> getAll() throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM permission");
            ResultSet res = preparedStatement.executeQuery();

            List<Permission> permissions = new ArrayList<>();
            while (res.next()) {
                permissions.add(new Permission(res.getLong(1), res.getString(2), res.getBoolean(3)));
            }
            return permissions;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean save(Permission permission) throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO permission (name , is_super) VALUES  (?,?)");

            preparedStatement.setString(1, permission.getName());
            preparedStatement.setBoolean(2, permission.isSuper());

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean update(Permission permission, String[] params) throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE permission SET name=?,is_super=? WHERE id=?");
            preparedStatement.setString(1, permission.getName());
            preparedStatement.setBoolean(2, permission.isSuper());
            preparedStatement.setLong(3, permission.getId());

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM permission WHERE id=?");
            preparedStatement.setLong(1, id);
            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }
}