package com.warehouse.Http;

import com.warehouse.DAO.DataBaseConnector;
import com.sun.net.httpserver.HttpServer;
import com.warehouse.Controller.*;
import com.warehouse.Service.RolePermissionService;
import com.warehouse.View.JsonView;
import com.warehouse.View.View;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    public static Logger root = LogManager.getRootLogger();

    public final static View VIEW = new JsonView();

    public static void main(String[] args) {
        new Server();
        try {
            System.out.println(Arrays.toString(RolePermissionService.getInstance().getRolePermissions(7).toArray()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Server() {
        try {

            root.info("Starting-up server...");

            // Init pool
            ThreadPoolExecutor pool = new ThreadPoolExecutor(
                    4, Runtime.getRuntime().availableProcessors() + 1, 5, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(100), Executors.defaultThreadFactory(),
                    new RejectedHandler()
            );

            root.info("Thread pool initialized...");

            // Init database connection
            try {
                DataBaseConnector.createConnector();
            } catch (SQLException e) {
                root.fatal("Problem with connection pool creation.");
                throw e;
            } catch (ClassNotFoundException e) {
                root.fatal("Database drivers not found.");
                throw e;
            }

            root.info("Database connected...");

            //Init view
            AbstractController.setView(VIEW);

            // Server start-up
            try {
                HttpServer server = HttpServer.create();

                //Server contexts
                //TODO Maybe Enum for this shit?
                server.createContext("/products", new ProductController());
                server.createContext("/groups", new GroupController());
                server.createContext("/manufacturers", new ManufacturerController());
                server.createContext("/users", new UserController());
                server.createContext("/roles", new RoleController());
                server.createContext("/roles/permissions", new RolePermissionController());
                server.createContext("/permissions", new PermissionController());
                server.createContext("/measures", new MeasureController());
                server.createContext("/login", new LoginController());

                server.bind(new InetSocketAddress(8089), 0);
                server.setExecutor(pool);
                server.start();

                root.info("Server start-up successful: " + server.getAddress());
            } catch (IOException e) {
                root.error("Server IO exception: " + e.getMessage());
            }
        } catch (Exception e) {
            root.fatal("Critical server exception: " + e.getMessage());
            System.exit(-1);
        }
    }
}
