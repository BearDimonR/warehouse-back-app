package com.warehouse.DAO;

import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductDAO implements DAO<Product> {
    public static ProductDAO instance;

    public synchronized static ProductDAO getInstance() {
        if (instance == null)
            instance = new ProductDAO();
        return instance;
    }

    private ProductDAO() {
    }

    @Override
    public Optional<Product> get(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next())
                return Optional.of(new Product(
                        res.getLong(1),
                        res.getString(2),
                        res.getFloat(3),
                        res.getFloat(4),
                        res.getFloat(5),
                        res.getString(6),
                        res.getInt(7),
                        res.getInt(8),
                        res.getString(9)));

            return Optional.empty();
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public List<Product> getAll(Filter filter, PageFilter pageFilter) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        String query = Stream.of(
                filter.inKeys("id"),
                filter.like())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));
        String where = query.isEmpty() ? "" : "WHERE " + query;
        String sql = String.format("SELECT * FROM product %s %s", where, pageFilter.page());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet res = preparedStatement.executeQuery();
            List<Product> product = new ArrayList<>();
            while (res.next()) {
                product.add(new Product(
                        res.getLong(1),
                        res.getString(2),
                        res.getFloat(3),
                        res.getFloat(4),
                        res.getFloat(5),
                        res.getString(6),
                        res.getInt(7),
                        res.getInt(8),
                        res.getString(9)));
            }
            return product;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public long save(Product product) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("INSERT INTO product (name , price, amount, total_cost, measure_name, group_products_id, manufacturer_id, description) " +
                            "VALUES  (?,?,?,?,?,?,?,?) RETURNING id");

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setDouble(3, product.getAmount());
            preparedStatement.setDouble(4, product.getTotalCost());
            preparedStatement.setString(5, product.getMeasureName());
            preparedStatement.setInt(6, product.getGroupId());
            preparedStatement.setInt(7, product.getManufacturerId());
            preparedStatement.setString(8, product.getDescription());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public boolean update(Product product) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE product SET name=?,price=?, amount=?, total_cost=?, measure_name=?, group_products_id=?, manufacturer_id=?, description=? WHERE id=?");
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setDouble(3, product.getAmount());
            preparedStatement.setDouble(4, product.getTotalCost());
            preparedStatement.setString(5, product.getMeasureName());
            preparedStatement.setInt(6, product.getGroupId());
            preparedStatement.setInt(7, product.getManufacturerId());
            preparedStatement.setString(8, product.getDescription());

            preparedStatement.setLong(9, product.getId());

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM product WHERE id=?");
            preparedStatement.setLong(1, id);

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }
}

