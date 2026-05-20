/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility class managing JDBC connections.
 * Includes automatic retry mechanisms for database availability.
 */
public class DBConnection {
    private static final Properties properties = new Properties();
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Warning: db.properties not found! Using default database configuration.");
                properties.setProperty("db.url", "jdbc:mysql://localhost:3306/airline_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                properties.setProperty("db.username", "root");
                properties.setProperty("db.password", "root");
            } else {
                properties.load(input);
            }
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to initialize database configurations: " + e.getMessage());
        }
    }

    /**
     * Retrieves a connection to the database.
     * Incorporates retry logic for fault-tolerant database connectivity.
     * @return Connection object
     * @throws SQLException if a database access error occurs after max retries
     */
    public static Connection getConnection() throws SQLException {
        int attempts = 0;
        while (true) {
            try {
                return DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
                );
            } catch (SQLException e) {
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    Logger.error("Database connection failed after " + MAX_RETRIES + " attempts.");
                    throw e;
                }
                Logger.warn("Database connection failed. Attempt " + attempts + "/" + MAX_RETRIES + ". Retrying in " + (RETRY_DELAY_MS / 1000) + "s...");
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Connection retry interrupted", ie);
                }
            }
        }
    }
}
