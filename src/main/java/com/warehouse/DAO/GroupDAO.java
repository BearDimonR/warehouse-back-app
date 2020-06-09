package com.warehouse.DAO;

import com.warehouse.Model.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDAO implements DAO<Group> {

    public static GroupDAO instance;

    public synchronized static GroupDAO getInstance() {
        if (instance == null)
            instance = new GroupDAO();
        return instance;
    }

    private Connection connection;

    private GroupDAO() {
    }

    @Override
    public Optional<Group> get(long id) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM group_products WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new Group(
                        res.getLong(1),
                        res.getString(2),
                        res.getString(3)));
            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public List<Group> getAll() throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM group_products");
            ResultSet res = preparedStatement.executeQuery();
            List<Group> groups = new ArrayList<>();
            while (res.next()) {
                groups.add(new Group(
                        res.getLong(1),
                        res.getString(2),
                        res.getString(3)));
            }
            return groups;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean save(Group group) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO group_products (name, description) VALUES (?,?)");
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getDescription());
            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean update(Group group, String[] params) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE group_products SET name = ?, description = ? WHERE id = ?");
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getDescription());
            preparedStatement.setLong(3, group.getId());
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM group_products WHERE id = ?");
            preparedStatement.setLong(1, id);
            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }
}
