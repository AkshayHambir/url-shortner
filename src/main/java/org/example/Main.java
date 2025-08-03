package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.database.Database;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger(Main.class);
        try {
            Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            Database.init();
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.setExecutor(null);
            server.start();

            logger.info("HTTP server started on port 8080");
            logger.info("H2 Console available at http://localhost:8082");
        } catch (SQLException e) {
           logger.error("Exception : {} - {}", e.getErrorCode(), e.getMessage());
        } catch (IOException e){
            logger.error("Exception : {}", e.getMessage());
        }
    }
}