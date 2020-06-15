package com.warehouse.DAO;

import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Credentials;
import com.warehouse.Model.User;

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

public class UserDAO implements DAO<User> {

    public static UserDAO instance;

    public synchronized static UserDAO getInstance() {
        if (instance == null)
            instance = new UserDAO();
        return instance;
    }

    private UserDAO() {
    }

    @Override
    public Optional<User> get(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user_account WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new User(
                        res.getLong(1),
                        res.getString(2),
                        res.getString(3),
                        res.getInt(4)));
            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }


    @Override
    public List<User> getAll(Filter filter, PageFilter pageFilter, OrderBy order) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        String query = Stream.of(
                filter.inKeys("id"),
                filter.like())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));
        String where = query.isEmpty() ? "" : "WHERE " + query;
        String sql = String.format("SELECT * FROM user_account %s %s %s",
                where,
                order.orderBy("id"),
                pageFilter.page());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet res = preparedStatement.executeQuery();
            List<User> user = new ArrayList<>();
            while (res.next()) {
                user.add(new User(res.getLong(1), res.getString(2), res.getString(3), res.getInt(4)));
            }
            return user;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    public Optional<User> getByCredentials(Credentials credentials) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user_account WHERE name = ? AND password = ?");
            preparedStatement.setString(1, credentials.getName());
            preparedStatement.setString(2, credentials.getPassword());
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new User(
                        res.getLong(1),
                        res.getString(2),
                        res.getString(3),
                        res.getInt(4)));
            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized long save(User user) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user_account (name , password, role_id) VALUES  (?,?,?) RETURNING id");

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getRoleId());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public synchronized boolean update(User user) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user_account SET name=?,password=?,role_id=? WHERE id=?");
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getRoleId());
            preparedStatement.setLong(4, user.getId());

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
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user_account WHERE id=?");
            preparedStatement.setLong(1, id);

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }
}
