package com.warehouse.Service;

import com.warehouse.DAO.UserDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Credentials;
import com.warehouse.Model.User;
import com.warehouse.Model.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public int getUsersCountByRole(long id) throws SQLException{
        String[] ids = new String[1];
        ids[0] = String.valueOf(id);
        List<Pair<String, String[]>> pairs = new ArrayList<>(3);
        Pair<String, String[]> pair = new Pair<>();
        pair.key = "role_id";
        pair.val = ids;
        pairs.add(pair);
        return UserDAO.getInstance().getAll(Filter.builder()
                .count(true)
                .params(pairs)
                .build(), new PageFilter()).size();
    }
}
