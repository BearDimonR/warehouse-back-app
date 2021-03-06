package com.warehouse.Http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.warehouse.Controller.*;
import com.warehouse.DAO.DataBaseConnector;
import com.warehouse.View.JsonView;
import com.warehouse.View.View;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final String HTTPS_PASSWORD = "server";
    public static Logger root = LogManager.getRootLogger();

    public final static View VIEW = new JsonView();

    public static void main(String[] args) {
        new Server();
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
                HttpsServer server = HttpsServer.create();
                
                //Init keys
                initHttps(server);

                //Server contexts
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

    private void initHttps(HttpsServer server) {
        try {

            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = HTTPS_PASSWORD.toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("server.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Set the SSL parameters
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
