package com.warehouse.Service;

import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Service<T> {
    Optional<T> get(long id) throws SQLException;

    List<T> getAll(Filter filter, PageFilter pageFilter, OrderBy order) throws SQLException;

    long count(Filter filter) throws SQLException;

    long create(T t) throws SQLException;

    boolean update(T t) throws SQLException;

    boolean delete(long id) throws SQLException;
}
