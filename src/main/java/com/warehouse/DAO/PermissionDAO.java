package com.warehouse.DAO;

import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermissionDAO implements DAO<Permission> {

    public static PermissionDAO instance;

    public synchronized static PermissionDAO getInstance() {
        if (instance == null)
            instance = new PermissionDAO();
        return instance;
    }

    private PermissionDAO() {
    }


    @Override
    public Optional<Permission> get(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
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
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    public Optional<List<Permission>> getUsersPermissions(Long userId) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
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
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public List<Permission> getAll(Filter filter, PageFilter pageFilter, OrderBy order) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        String query = Stream.of(
                filter.inKeys("id"),
                filter.like())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));
        String where = query.isEmpty() ? "" : "WHERE " + query;
        String sql = String.format("SELECT * FROM permission %s %s %s",
                where,
                order.orderBy("id"),
                pageFilter.page());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet res = preparedStatement.executeQuery();
            List<Permission> permissions = new ArrayList<>();
            while (res.next()) {
                permissions.add(new Permission(res.getLong(1), res.getString(2), res.getBoolean(3)));
            }
            return permissions;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized long save(Permission permission) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO permission (name , is_super) VALUES  (?,?) RETURNING id");
            preparedStatement.setString(1, permission.getName());
            preparedStatement.setBoolean(2, permission.getIsSuper());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized boolean update(Permission permission) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE permission SET name=?,is_super=? WHERE id=?");
            preparedStatement.setString(1, permission.getName());
            preparedStatement.setBoolean(2, permission.getIsSuper());
            preparedStatement.setLong(3, permission.getId());

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM permission WHERE id=?");
            preparedStatement.setLong(1, id);
            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }
}