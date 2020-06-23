package com.warehouse.DAO;


import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Measure;

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

public class MeasureDAO implements DAO<Measure> {

    public static MeasureDAO instance;

    public synchronized static MeasureDAO getInstance() {
        if (instance == null)
            instance = new MeasureDAO();
        return instance;
    }

    private MeasureDAO() {
    }

    @Override
    public List<Measure> getAll(Filter filter, PageFilter pageFilter, OrderBy order) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        String query = Stream.of(
                filter.inKeys("name"),
                filter.like())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));
        String where = query.isEmpty() ? "" : "WHERE " + query;
        String sql = String.format("SELECT * FROM measure %s %s %s",
                where,
                order.orderBy("name"),
                pageFilter.page());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet res = preparedStatement.executeQuery();
            List<Measure> measures = new ArrayList<>();
            while (res.next()) {
                measures.add(new Measure(res.getString(1)));
            }
            return measures;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public Optional<Measure> get(long id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public long save(Measure measure) throws SQLException {
        return -1;
    }

    @Override
    public boolean update(Measure measure) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(long id) throws SQLException {
        return false;
    }
}
