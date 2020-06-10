package com.warehouse.DAO;

import com.warehouse.Utils.DBProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseConnector {
    private final static  String URL = DBProperties.getProperty("db_url");
    private final static String USERNAME = DBProperties.getProperty("username");
    private final static String PASSWORD = DBProperties.getProperty("password");

    private static Logger logger = LogManager.getLogger(DataBaseConnector.class);

    private static DataBaseConnector connector;

    public static DataBaseConnector getConnector() {
        return connector;
    }

    public static void createConnector() throws SQLException, ClassNotFoundException {
        if(connector == null)
            connector = new DataBaseConnector();
    }

    private ConnectionPool pool;

    private DataBaseConnector() throws ClassNotFoundException, SQLException {
            Class.forName("org.postgresql.Driver");
            pool = ConnectionPool.create(URL, USERNAME, PASSWORD);
    }

    public Connection getConnection() {
        return pool.getConnection();
    }

    public void releaseConnection(Connection connection) throws SQLException {
        try {
            pool.releaseConnection(connection);
        } catch (SQLException e) {
            logger.error("Problem with releasing connection:" + e.getMessage());
            throw e;
        }
    }
}