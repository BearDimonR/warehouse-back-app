package com.warehouse.DAO;

import com.warehouse.Model.Manufacturer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManufacturerDAO implements DAO<Manufacturer> {

    public static ManufacturerDAO instance;

    public synchronized static ManufacturerDAO getInstance() {
        if (instance == null)
            instance = new ManufacturerDAO();
        return instance;
    }

    Connection connection;

    private ManufacturerDAO() {
    }

    @Override
    public Optional<Manufacturer> get(long id) throws SQLException {
        connection = DataBaseConnector.getConnector().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM manufacturer WHERE id = ?");
        preparedStatement.setLong(1, id);
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getConnector().releaseConnection(connection);
        connection = null;
        if (res.next())
            return Optional.of(new Manufacturer(
                    res.getLong(1),
                    res.getString(2)));
        return Optional.empty();
    }

    @Override
    public List<Manufacturer> getAll() throws SQLException {
        connection = DataBaseConnector.getConnector().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM manufacturer");
        ResultSet res = preparedStatement.executeQuery();
        DataBaseConnector.getConnector().releaseConnection(connection);
        connection = null;
        List<Manufacturer> manufacturers = new ArrayList<>();
        while (res.next()) {
            manufacturers.add(new Manufacturer(
                    res.getLong(1),
                    res.getString(2)));
        }
        return manufacturers;
    }

    @Override
    public synchronized boolean save(Manufacturer manufacturer) throws SQLException {
        connection = DataBaseConnector.getConnector().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO manufacturer (name) VALUES (?)");
        preparedStatement.setString(1, manufacturer.getName());
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getConnector().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean update(Manufacturer manufacturer, String[] params) throws SQLException {
        connection = DataBaseConnector.getConnector().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("UPDATE manufacturer SET name = ? WHERE id = ?");
        preparedStatement.setString(1, manufacturer.getName());
        preparedStatement.setLong(2, manufacturer.getId());
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getConnector().releaseConnection(connection);
        connection = null;
        return res != 0;
    }

    @Override
    public synchronized boolean delete(long id) throws SQLException {
        connection = DataBaseConnector.getConnector().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM manufacturer WHERE id = ?");
        preparedStatement.setLong(1, id);
        int res = preparedStatement.executeUpdate();
        DataBaseConnector.getConnector().releaseConnection(connection);
        connection = null;
        return res != 0;
    }
}
