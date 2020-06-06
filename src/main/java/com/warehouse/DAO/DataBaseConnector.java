package com.warehouse.DAO;

import com.warehouse.ConnectionPool;
import com.warehouse.utils.DBProperties;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseConnector {
    private final static  String URL = DBProperties.getProperty("db_url");
    private final static String USERNAME = DBProperties.getProperty("username");
    private final static String PASSWORD = DBProperties.getProperty("password");

    private static DataBaseConnector connector;

    public static DataBaseConnector getInstance() {
        if(connector == null)
            initConnector();
        return connector;
    }

    public static void initConnector() {
        if(connector == null)
            connector = new DataBaseConnector();
    }

    private ConnectionPool pool;

    private DataBaseConnector() {
        try {
            Class.forName("org.postgresql.Driver");
            pool = ConnectionPool.create(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver loading problem!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database pool creating problem!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return pool.getConnection();
    }

    public void releaseConnection(Connection connection) {
        try {
            pool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("Problem with releasing connection");
            e.printStackTrace();
        }
    }
}