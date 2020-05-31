package com.warehouse.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnector {
    private final static  String URL = "jdbc:postgresql://localhost:5432/storage";
    private final static String USERNAME = "server";
    private final static String PASSWORD = "admin";

    private static DataBaseConnector connector;

    public static DataBaseConnector getConnector() {
        return connector;
    }

    public static void initConnector() throws ClassNotFoundException {
        if(connector == null)
            connector = new DataBaseConnector();
    }

    private DataBaseConnector() throws ClassNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver loading problem!");
            throw e;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e){
            System.err.println("Database connection problem!");
            throw e;
        }
    }
}
