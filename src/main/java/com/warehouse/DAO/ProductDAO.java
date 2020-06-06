package com.warehouse.DAO;

import com.warehouse.Model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO implements DAO<Product> {
    public static ProductDAO instance;

    public synchronized static ProductDAO getInstance() {
        if (instance == null)
            instance = new ProductDAO();
        return instance;
    }

    Connection connection;

    private ProductDAO() {
    }

    @Override
    public synchronized Optional<Product> get(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE id = ?");
        preparedStatement.setLong(1, id);
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        if (res.next())
            return Optional.of(new Product(
                    res.getLong(1),
                    res.getString(2),
                    res.getDouble(3),
                    res.getDouble(4),
                    res.getDouble(5),
                    res.getString(6),
                    res.getInt(7),
                    res.getInt(8),
                    res.getString(9)));

        return Optional.empty();
    }

    @Override
    public synchronized List<Product> getAll() throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product");
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        List<Product> product = new ArrayList<>();
        while (res.next()) {
            product.add(new Product(
                    res.getLong(1),
                    res.getString(2),
                    res.getDouble(3),
                    res.getDouble(4),
                    res.getDouble(5),
                    res.getString(6),
                    res.getInt(7),
                    res.getInt(8),
                    res.getString(9)));
        }
        return product;
    }

    @Override
    public synchronized boolean save(Product product) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO product (name , price, amount, total_cost, measure_name, group_products_id, manufacturer_id, description) VALUES  (?,?,?,?,?,?,?,?)");

        preparedStatement.setString(1, product.getName());
        preparedStatement.setDouble(2, product.getPrice());
        preparedStatement.setDouble(3, product.getAmount());
        preparedStatement.setDouble(4, product.getTotalCost());
        preparedStatement.setString(5, product.getMeasureName());
        preparedStatement.setInt(6, product.getGroupProductId());
        preparedStatement.setInt(7, product.getManufactureId());
        preparedStatement.setString(8, product.getDescription());

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean update(Product product, String[] params) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE product SET name=?,price=?, amount=?, total_cost=?, measure_name=?, group_products_id=?, manufacturer_id=?, description=? WHERE id=?");
        preparedStatement.setString(1, product.getName());
        preparedStatement.setDouble(2, product.getPrice());
        preparedStatement.setDouble(3, product.getAmount());
        preparedStatement.setDouble(4, product.getTotalCost());
        preparedStatement.setString(5, product.getMeasureName());
        preparedStatement.setInt(6, product.getGroupProductId());
        preparedStatement.setInt(7, product.getManufactureId());
        preparedStatement.setString(8, product.getDescription());

        preparedStatement.setLong(9, product.getId());

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        connection = DataBaseConnector.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM product WHERE id=?");
        preparedStatement.setLong(1, id);

        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getInstance().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

}
