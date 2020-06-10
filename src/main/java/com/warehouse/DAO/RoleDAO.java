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
        if (instance == null)
            instance = new RoleDAO();
        return instance;
    }

    private RoleDAO() {
    }

    @Override
    public Optional<Role> get(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM role WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new Role(
                        res.getLong(1),
                        res.getString(2),
                        res.getBoolean(3)));
            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
	}

    public Optional<Role> getUserRole(long userId) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM role WHERE id = (SELECT role_id FROM user_account WHERE id = ?)");
            preparedStatement.setLong(1, userId);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new Role(
                        res.getLong(1),
                        res.getString(2),
                        res.getBoolean(3)));
            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public List<Role> getAll() throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM role");
            ResultSet res = preparedStatement.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (res.next()) {
                roles.add(new Role(
                        res.getLong(1),
                        res.getString(2),
                        res.getBoolean(3)));
            }
            return roles;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized long save(Role role) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO role (name, is_super) VALUES (?,?) RETURNING id");
            preparedStatement.setString(1, role.getName());
            preparedStatement.setBoolean(2, role.is_super());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized boolean update(Role role, String[] params) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE role SET name = ?, is_super = ? WHERE id = ?");
            preparedStatement.setString(1, role.getName());
            preparedStatement.setBoolean(2, role.is_super());
            preparedStatement.setLong(3, role.getId());
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM role WHERE id = ?");
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }
}
