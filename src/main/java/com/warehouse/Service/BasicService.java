package com.warehouse.Service;

import com.warehouse.DAO.DAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BasicService<T> implements Service<T> {

    public DAO<T> dao;

    @Override
    public Optional<T> get(long id) throws SQLException {
        return dao.get(id);
    }

    @Override
    public List<T> getAll(Filter filter, PageFilter pageFilter, OrderBy order) throws SQLException {
        return dao.getAll(filter, pageFilter, order);
    }

    @Override
    public long count(Filter filter) throws SQLException {
        return dao.getAll(filter, new PageFilter(), new OrderBy()).size();
    }

    @Override
    public long create(T t) throws SQLException {
        return dao.save(t);
    }

    @Override
    public boolean update(T t) throws SQLException {
        return dao.update(t);
    }

    @Override
    public boolean delete(long id) throws SQLException {
        return dao.delete(id);
    }
}
