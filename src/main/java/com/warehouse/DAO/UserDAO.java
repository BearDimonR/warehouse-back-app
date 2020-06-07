package com.warehouse.DAO;

import com.warehouse.Model.Permission;
import com.warehouse.Model.auth.Credentials;
import com.warehouse.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User> {

    public static UserDAO instance;

    public synchronized static UserDAO getInstance() {
        if (instance == null)
            instance = new UserDAO();
        return instance;
    }

    Connection connection;

    private UserDAO() {
    }

    @Override
    public synchronized Optional<User> get(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user_account WHERE id = ?");
        preparedStatement.setLong(1, id);
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        if (res.next())
            return Optional.of(new User(
                    res.getLong(1),
                    res.getString(2),
                    res.getString(3),
                    res.getInt(4)));
        return Optional.empty();
    }

    @Override
    public synchronized List<User> getAll() throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user_account");
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        List<User> user = new ArrayList<>();
        while (res.next()) {
            user.add(new User(res.getLong(1), res.getString(2), res.getString(3), res.getInt(4)));
        }
        return user;
    }

    public synchronized Optional<User> getByCredentials(Credentials credentials) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user_account WHERE name = ? AND password = ?");
        preparedStatement.setString(1, credentials.getName());
        preparedStatement.setString(2, credentials.getPassword());
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        if (res.next())
            return Optional.of(new User(
                    res.getLong(1),
                    res.getString(2),
                    res.getString(3),
                    res.getInt(4)));
        return Optional.empty();
    }

    @Override
    public synchronized boolean save(User user) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user_account (name , password, role_is) VALUES  (?,?,?)");

        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setInt(3, user.getRoleId());

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean update(User user, String[] params) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user_account SET name=?,password=?,role_id=? WHERE id=?");
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setInt(3, user.getRoleId());
        preparedStatement.setLong(4, user.getId());

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user_account WHERE id=?");
        preparedStatement.setLong(1, id);

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }
}
