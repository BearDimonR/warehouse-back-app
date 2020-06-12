package com.warehouse.Service;

import com.warehouse.DAO.UserDAO;
import com.warehouse.Model.Credentials;
import com.warehouse.Model.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserService extends BasicService<User> {
    public static UserService instance;

    public synchronized static UserService getInstance() {
        if (instance == null)
            instance = new UserService();
        return instance;
    }

    private UserService() {
        dao = UserDAO.getInstance();
    }

    public Optional<User> getByCredentials(Credentials fromJson) throws SQLException {
        return UserDAO.getInstance().getByCredentials(fromJson);
    }
}
