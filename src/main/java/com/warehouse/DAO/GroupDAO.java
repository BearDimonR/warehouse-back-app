package com.warehouse.DAO;

import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Group;

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

public class GroupDAO implements DAO<Group> {

    public static GroupDAO instance;

    public synchronized static GroupDAO getInstance() {
        if (instance == null)
            instance = new GroupDAO();
        return instance;
    }

    private GroupDAO() {
    }

    @Override
    public Optional<Group> get(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM group_products WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(Group.builder()
                        .id(res.getLong(1))
                        .name(res.getString(2))
                        .description(res.getString(3))
                        .build());
            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public List<Group> getAll(Filter filter, PageFilter pageFilter) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        String query = Stream.of(
                filter.inKeys("id"),
                filter.like())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));
        String where = query.isEmpty()?"":"WHERE " + query;
        String sql = String.format("SELECT * FROM group_products %s %s", where, pageFilter.page());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet res = preparedStatement.executeQuery();
            List<Group> groups = new ArrayList<>();
            while (res.next()) {
                groups.add(Group.builder()
                        .id(res.getLong(1))
                        .name(res.getString(2))
                        .description(res.getString(3))
                        .build());
            }
            return groups;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public long save(Group group) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement
                            ("INSERT INTO group_products (name, description) VALUES (?,?) RETURNING id");
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getDescription());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public boolean update(Group t) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE group_products SET name=?, description=? WHERE id = ?");
            preparedStatement.setString(1, t.getName());
            preparedStatement.setString(2, t.getDescription());
            preparedStatement.setLong(3, t.getId());
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM group_products WHERE id = ?");
            preparedStatement.setLong(1, id);
            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }
}
