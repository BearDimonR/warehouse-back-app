package com.warehouse;

import com.warehouse.DAO.DataBaseConnector;
import com.sun.net.httpserver.HttpServer;
import com.warehouse.Handler.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        // Init logger
        Logger root = LogManager.getRootLogger();
        root.info("Starting-up server...");

        // Init pool
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                4, Runtime.getRuntime().availableProcessors() + 1, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100), Executors.defaultThreadFactory(),
                new RejectedHandler()
        );

        // Init database connection
        DataBaseConnector.initConnector();

        // Server start-up
        try {
            HttpServer server = HttpServer.create();

            //Server contexts
            //TODO Maybe Enum for this shit?
            server.createContext("/warehouse/user/products", new ProductHandler());
            server.createContext("/warehouse/user/groups", new GroupHandler());
            server.createContext("/warehouse/admin/manufacturers", new ManufacturerHandler());
            server.createContext("/warehouse/admin/users", new UserHandler());
            server.createContext("/warehouse/admin/roles", new RoleHandler());
            server.createContext("/warehouse/admin/roles/permissions", new RolePermissionHandler());
            server.createContext("/warehouse/admin/permissions", new PermissionHandler());
            server.createContext("/warehouse/login", new LoginHandler());

            server.bind(new InetSocketAddress(8089), 0);
            server.setExecutor(pool);
            server.start();
            root.info("Server start-up successful: " + server.getAddress());
        } catch (IOException e) {
            root.trace("Server exception", e);
        }
    }
}
