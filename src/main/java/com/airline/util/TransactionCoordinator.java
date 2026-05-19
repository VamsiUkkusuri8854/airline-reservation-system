package com.airline.util;

import com.airline.exception.AirlineException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility orchestrator managing JDBC transactional boundaries.
 * Automatically initiates connection, disables auto-commit, rolls back on failures,
 * and recovers connection state before cleanup.
 */
public class TransactionCoordinator {

    /**
     * Executes a series of database operations atomically within a single transaction.
     * @param callback Lambda operations to run
     * @param <T> Return type of the transaction execution
     * @return Result of the operations
     * @throws AirlineException if any business or SQL error occurs
     */
    public static <T> T execute(TransactionCallback<T> callback) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            T result = callback.execute(conn);

            conn.commit(); // Success: commit
            return result;
        } catch (SQLException e) {
            rollback(conn);
            Logger.error("SQL Transaction failed, changes rolled back", e);
            throw new AirlineException("Database transaction failed: " + e.getMessage(), e);
        } catch (AirlineException e) {
            rollback(conn);
            Logger.warn("Business rule violation, transaction rolled back: " + e.getMessage());
            throw e; // Rethrow business exceptions
        } catch (Exception e) {
            rollback(conn);
            Logger.error("Unexpected error, transaction rolled back", e);
            throw new AirlineException("Unexpected transaction failure: " + e.getMessage(), e);
        } finally {
            cleanup(conn);
        }
    }

    private static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                Logger.info("Transaction successfully rolled back.");
            } catch (SQLException ex) {
                Logger.error("Failed to rollback transaction", ex);
            }
        }
    }

    private static void cleanup(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true); // Restore default state
                conn.close();
            } catch (SQLException e) {
                Logger.error("Failed to restore auto-commit or close connection", e);
            }
        }
    }
}
