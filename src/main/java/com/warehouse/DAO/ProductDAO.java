package com.warehouse.DAO;

import com.warehouse.Model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

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
    public Optional<Product> get(long id) throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
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
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public List<Product> getAll() throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product");
            ResultSet res = preparedStatement.executeQuery();
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
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean save(Product product) throws SQLException {
        try {
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
            return res != 0;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean update(Product product, String[] params) throws SQLException {
        try {
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
            return res != 0;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        try {
            connection = DataBaseConnector.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM product WHERE id=?");
            preparedStatement.setLong(1, id);

            int res = preparedStatement.executeUpdate();
            return res != 0;
        } finally {
            DataBaseConnector.getInstance().releaseConnection(connection);
            connection = null;
        }
    }

    public List<Product> filter(String name, String group, String manufacturer) throws SQLException {
        Filter filter = new Filter() {
            @Override
            public ResultSet filterByName(String name) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE name LIKE '?%'");
                return preparedStatement.executeQuery();
            }

            @Override
            public ResultSet filterByGroup(int groupId) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE group_products_id LIKE '" + groupId + "%'");
                return preparedStatement.executeQuery();
            }

            @Override
            public ResultSet filterByManufacturer(int manufacturerId) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE manufacturer_id LIKE '" + manufacturerId + "%'");
                preparedStatement.executeQuery();
                return preparedStatement.executeQuery();
            }
        };

        connection = DataBaseConnector.getInstance().getConnection();

        TreeMap<Product, Integer> productMap = new TreeMap<>();
        List<Product> productList = new ArrayList<>();
        List<ResultSet> resArr = new ArrayList<>();
        int fields = 0, groupId = 0, manufacturerId = 0;

        if (name != null) {
            fields++;
            resArr.add(filter.filterByName(name));
        }
        if (group != null) {
            fields++;
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM group_products WHERE name=" + group);
            ResultSet resultSet = ps.executeQuery();
            groupId = resultSet.getInt(1);
            resArr.add(filter.filterByGroup(groupId));
        }
        if (manufacturer != null) {
            fields++;
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM manufacturer WHERE name=" + manufacturer);
            ResultSet resultSet = ps.executeQuery();
            manufacturerId = resultSet.getInt(1);
            resArr.add(filter.filterByManufacturer(manufacturerId));
        }
//todo check is correct ? 'cycle while'
        for (int i = 0; i < resArr.size(); i++) {
            while (resArr.get(i).next()) {
                Product product = new Product(
                        resArr.get(i).getLong(1),
                        resArr.get(i).getString(2),
                        resArr.get(i).getDouble(3),
                        resArr.get(i).getDouble(4),
                        resArr.get(i).getDouble(5),
                        resArr.get(i).getString(6),
                        resArr.get(i).getInt(7),
                        resArr.get(i).getInt(8),
                        resArr.get(i).getString(9));
                if (!productMap.containsKey(product)) productMap.put(product, 1);
                else {
                    productMap.replace(product, 1 + productMap.get(product));
                    if (productMap.get(product) == fields) productList.add(product);
                }
            }
        }
        return productList;
    }

}

interface Filter {
    ResultSet filterByName(String name) throws SQLException;

    ResultSet filterByGroup(int group) throws SQLException;

    ResultSet filterByManufacturer(int manufacturer) throws SQLException;
}

