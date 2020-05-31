package com.warehouse;

import com.warehouse.DAO.DataBaseConnector;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) {
        Server server = new Server();
    }

    private Server() {
        //Init database connection
        try {
            DataBaseConnector.initConnector();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //TODO SERVER START-UP
        HttpServer server = null;
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(8089), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
