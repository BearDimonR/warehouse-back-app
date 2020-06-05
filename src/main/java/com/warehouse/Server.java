package com.warehouse;

import com.warehouse.DAO.DataBaseConnector;
import com.sun.net.httpserver.HttpServer;
import com.warehouse.Handler.*;
import com.warehouse.Model.Role;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Http server for storage app
 */
public class Server {

    public static void main(String[] args) {
        new Server();
    }

    private ThreadPoolExecutor pool;

    private Server() {
        // Init pool
        pool = new ThreadPoolExecutor(
                4, Runtime.getRuntime().availableProcessors() + 1, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100), Executors.defaultThreadFactory(),
                new RejectedHandler()
        );

        // Init database connection
        DataBaseConnector.initConnector();
        // Server start-up
        try {
            HttpServer server = HttpServer.create();

            // Server contexts
            // TODO Maybe Enum for this shit?
            server.createContext("/warehouse/user/products", new ProductHandler());
            server.createContext("/warehouse/user/groups", new GroupHandler());
            server.createContext("/warehouse/admin/manufacturers", new ManufacturerHandler());
            server.createContext("/warehouse/admin/users", new UserHandler());
            server.createContext("/warehouse/admin/roles", new RoleHandler());
            server.createContext("/warehouse/admin/permissions", new PermissionHandler());

            server.bind(new InetSocketAddress(8089), 0);
            server.setExecutor(pool);
            server.start();
            System.out.println("Server was started: " + server.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
