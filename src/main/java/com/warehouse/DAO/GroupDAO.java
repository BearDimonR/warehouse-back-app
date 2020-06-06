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
        if(instance == null)
            instance = new GroupDAO();
        return instance;
    }

    private Connection connection;

    private GroupDAO() {}

    @Override
    public Optional<Group> get(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM group_products WHERE id = ?");
        preparedStatement.setLong(1, id);
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        if(res.next())
            return Optional.of(new Group(
                    res.getLong(1),
                    res.getString(2),
                    res.getString(3)));
        return Optional.empty();
    }

    @Override
    public List<Group> getAll() throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM group_products");
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        List<Group> groups = new ArrayList<>();
        while (res.next()) {
            groups.add(new Group(
                    res.getLong(1),
                    res.getString(2),
                    res.getString(3)));
        }
        return groups;
    }

    @Override
    public boolean save(Group group) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO group_products (name, description) VALUES (?,?)");
        preparedStatement.setString(1, group.getName());
        preparedStatement.setString(2, group.getDescription());
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public boolean update(Group group, String[] params) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("UPDATE group_products SET name = ?, description = ? WHERE id = ?");
        preparedStatement.setString(1, group.getName());
        preparedStatement.setString(2, group.getDescription());
        preparedStatement.setLong(3, group.getId());
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public boolean delete(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM group_products WHERE id = ?");
        preparedStatement.setLong(1, id);
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }
}
