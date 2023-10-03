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

    private final String dbUrl;
    private final String user;
    private final String pass;
    private final String dbDriver;

    public ConnectionManager(@Value("${database.url}")
                             String dbUrl,
                             @Value("${database.username}")
                             String user,
                             @Value("${database.password}")
                             String pass,
                             @Value("${database.driver-class-name}")
                             String dbDriver)

    {
        this.dbUrl = dbUrl;
        this.user = user;
        this.pass = pass;
        this.dbDriver = dbDriver;
    }

    private Connection connection;

    public Connection getConnection() {
        if (!isConnectionValid()) {
            getValidConnection();
        }

        return connection;
    }

    private synchronized void getValidConnection() {
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
            log.error("Error while destroy",e);
        }
    }
}

