package com.example.unavailabledb.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@Component
public class ConnectionManager {

    @Value("${database.url}")
    private String dbUrl;
    @Value("${database.username}")
    private String user;
    @Value("${database.password}")
    private String pass;
    @Value("${database.driver-class-name}")
    private String dbDriver;
    private static Connection connection;

    public Connection getConnection() {
        if (!isConnectionValid()) {
            getValidConnection();
        }

        return connection;
    }

    public synchronized void getValidConnection() {
        while (!isConnectionValid()) {
            connection = createConnection();
        }
    }

    private Connection createConnection() {
        log.info("Creating connection to database.");
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            log.error("Exception while registering jdbc driver", e);
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, user, pass);
            log.info("Connection to database created.");

        } catch (SQLException e) {
            log.error("Exception while connection to database", e);
        }
        return connection;
    }

    public boolean isConnectionValid()
    {
        try {
            if (connection == null || connection.isClosed()) {
                log.warn("Connection to database is closed");
                return false;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @PreDestroy
    private void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }
}

